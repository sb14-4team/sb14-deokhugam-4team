// 덕후감 사용자 흐름 부하. PROFILE=smoke|load|stress|soak 로 단계를 고른다 (기본 smoke)
//   k6 run -e PROFILE=load loadtest/k6/scenario.js
// 외부 API(카카오 ISBN·OCR·S3 업로드)는 한도·비용 때문에 넣지 않는다
import { check, sleep } from 'k6';
import { get, pick, post, query, randInt, SCALE, seedId, TOPICS } from './lib.js';

const PROFILES = {
  smoke: { executor: 'constant-vus', vus: 2, duration: '1m' },
  // 평소: 동시 사용자 30명 (설계 가정 '수십 명 이하')
  load: {
    executor: 'ramping-vus',
    stages: [
      { duration: '1m', target: 30 },
      { duration: '10m', target: 30 },
      { duration: '30s', target: 0 },
    ],
  },
  // 꺾이는 지점 찾기. 실패 기준을 넘는 게 정상이고, 어느 계단에서 넘는지를 본다
  stress: {
    executor: 'ramping-vus',
    stages: [
      { duration: '2m', target: 10 },
      { duration: '3m', target: 50 },
      { duration: '3m', target: 100 },
      { duration: '3m', target: 150 },
      { duration: '1m', target: 0 },
    ],
  },
  // 오래 버티기: 힙이 GC 뒤 제자리로 오는지, 로그·ES 디스크가 어떻게 느는지
  soak: {
    executor: 'ramping-vus',
    stages: [
      { duration: '1m', target: 30 },
      { duration: '60m', target: 30 },
      { duration: '30s', target: 0 },
    ],
  },
};

const profile = __ENV.PROFILE || 'smoke';
if (!PROFILES[profile]) throw new Error(`PROFILE은 ${Object.keys(PROFILES).join('|')} 중 하나`);

export const options = {
  scenarios: { [profile]: PROFILES[profile] },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    'http_req_duration{kind:read}': ['p(95)<500'],
    'http_req_duration{kind:write}': ['p(95)<1000'],
    checks: ['rate>0.99'],
  },
  summaryTrendStats: ['avg', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

const ok = (res, name) => check(res, { [`${name} 2xx`]: (r) => r.status >= 200 && r.status < 300 });

function searchBooks(me) {
  const params = {
    keyword: Math.random() < 0.7 ? pick(TOPICS) : undefined,
    orderBy: pick(['title', 'publishedDate', 'rating', 'reviewCount']),
    direction: pick(['DESC', 'ASC']),
    limit: 20,
  };
  const res = get(`/api/books?${query(params)}`, 'GET /api/books');
  ok(res, 'GET /api/books');
  // 셋 중 하나는 다음 페이지까지 넘긴다 (커서 쿼리)
  if (res.status === 200 && Math.random() < 0.3) {
    const body = res.json();
    if (body.hasNext) {
      const next = get(
        `/api/books?${query({ ...params, cursor: body.nextCursor, after: body.nextAfter })}`,
        'GET /api/books (next)',
      );
      ok(next, 'GET /api/books (next)');
    }
  }
}

function searchReviews(me) {
  const by = Math.random();
  const params = {
    keyword: by < 0.4 ? pick(TOPICS) : undefined,
    bookId: by >= 0.4 && by < 0.8 ? seedId('book', randInt(SCALE.books)) : undefined,
    userId: by >= 0.8 ? seedId('user', randInt(SCALE.users)) : undefined,
    orderBy: pick(['createdAt', 'rating']),
    direction: 'DESC',
    limit: 20,
  };
  ok(get(`/api/reviews?${query(params)}`, 'GET /api/reviews', me), 'GET /api/reviews');
}

function readReview(me) {
  const reviewId = seedId('review', randInt(SCALE.reviews));
  ok(get(`/api/reviews/${reviewId}`, 'GET /api/reviews/{id}', me), 'GET /api/reviews/{id}');
  ok(get(`/api/comments?${query({ reviewId, limit: 20 })}`, 'GET /api/comments'), 'GET /api/comments');
}

function notifications(me) {
  ok(get(`/api/notifications?${query({ userId: me, limit: 20 })}`, 'GET /api/notifications'), 'GET /api/notifications');
}

function details() {
  ok(get(`/api/books/${seedId('book', randInt(SCALE.books))}`, 'GET /api/books/{id}'), 'GET /api/books/{id}');
  ok(get(`/api/users/${seedId('user', randInt(SCALE.users))}`, 'GET /api/users/{id}'), 'GET /api/users/{id}');
}

function writeComment(me) {
  const res = post('/api/comments', 'POST /api/comments', {
    reviewId: seedId('review', randInt(SCALE.reviews)),
    userId: me,
    content: `부하 테스트 댓글 ${Date.now()}`,
  });
  ok(res, 'POST /api/comments');
}

function toggleLike(me) {
  const reviewId = seedId('review', randInt(SCALE.reviews));
  ok(post(`/api/reviews/${reviewId}/like`, 'POST /api/reviews/{id}/like', null, me), 'POST /api/reviews/{id}/like');
}

function writeReview(me) {
  // 이미 쓴 도서를 고르면 409(1인 1리뷰). 실패로 세지 않는다
  const res = post(
    '/api/reviews',
    'POST /api/reviews',
    { userId: me, bookId: seedId('book', randInt(SCALE.books)), rating: randInt(5), content: `부하 테스트 리뷰 ${Date.now()}` },
    null,
    [201, 409],
  );
  check(res, { 'POST /api/reviews 201|409': (r) => r.status === 201 || r.status === 409 });
}

// 읽기 85 : 쓰기 15
const ACTIONS = [
  [30, searchBooks],
  [25, searchReviews],
  [15, readReview],
  [10, notifications],
  [5, details],
  [7, writeComment],
  [5, toggleLike],
  [3, writeReview],
];
const TOTAL = ACTIONS.reduce((sum, [w]) => sum + w, 0);

export default function () {
  const me = seedId('user', randInt(SCALE.users));
  let roll = Math.random() * TOTAL;
  for (const [weight, action] of ACTIONS) {
    roll -= weight;
    if (roll < 0) {
      action(me);
      break;
    }
  }
  sleep(1 + Math.random() * 2); // 사람이 화면을 읽는 시간
}
