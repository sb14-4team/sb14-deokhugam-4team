// 동시성: 서로 다른 사용자 VUS명이 같은 리뷰 하나에 동시에 좋아요와 댓글을 단다
// 끝나고 likeCount·commentCount가 정확히 VUS인지 본다 (원자 UPDATE가 맞게 도는지)
//   k6 run loadtest/k6/race.js
import { check, fail } from 'k6';
import exec from 'k6/execution';
import { get, post, SCALE, seedId } from './lib.js';

const VUS = Number(__ENV.VUS || 50);

export const options = {
  scenarios: {
    race: { executor: 'per-vu-iterations', vus: VUS, iterations: 1, maxDuration: '1m' },
  },
  thresholds: { checks: ['rate==1'] },
};

// 새 리뷰를 하나 만들어 0에서 시작한다. 시드 사용자 1번이 아직 안 쓴 도서를 찾는다
export function setup() {
  const author = seedId('user', 1);
  for (let i = 0; i < 50; i++) {
    const bookId = seedId('book', Math.floor(Math.random() * SCALE.books) + 1);
    const res = post('/api/reviews', 'POST /api/reviews', { userId: author, bookId, rating: 5, content: '동시성 테스트 리뷰' }, null, [201, 409]);
    if (res.status === 201) return { reviewId: res.json('id'), author };
  }
  fail('리뷰를 만들 도서를 찾지 못했다');
}

export default function ({ reviewId }) {
  // VU마다 다른 사용자 (작성자 1번은 건너뛴다)
  const me = seedId('user', exec.vu.idInTest + 1);
  const like = post(`/api/reviews/${reviewId}/like`, 'POST /api/reviews/{id}/like', null, me);
  check(like, { 'like 200 & liked': (r) => r.status === 200 && r.json('liked') === true });
  const comment = post('/api/comments', 'POST /api/comments', { reviewId, userId: me, content: '동시 댓글' });
  check(comment, { 'comment 2xx': (r) => r.status >= 200 && r.status < 300 });
}

export function teardown({ reviewId, author }) {
  const res = get(`/api/reviews/${reviewId}`, 'GET /api/reviews/{id}', author);
  const body = res.json();
  console.log(`reviewId=${reviewId} likeCount=${body.likeCount} commentCount=${body.commentCount} (기대 ${VUS})`);
  check(body, {
    [`likeCount == ${VUS}`]: (b) => b.likeCount === VUS,
    [`commentCount == ${VUS}`]: (b) => b.commentCount === VUS,
  });
}
