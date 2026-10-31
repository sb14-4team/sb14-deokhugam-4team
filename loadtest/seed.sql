-- 부하 테스트용 시드. 기본값은 M 규모(사용자 1,000 · 도서 3,000 · 리뷰 30,000 · 댓글 100,000 · 좋아요 200,000)
-- 실행: docker compose exec -T postgres psql -U deokhugam -d deokhugam -v ON_ERROR_STOP=1 < loadtest/seed.sql
-- 규모 바꾸기: psql에 -v users=100 -v books=300 ... 를 붙인다 (S 규모 예시는 loadtest/README.md)
--
-- 테이블은 앱이 한 번 떠서 ddl-auto로 만든 뒤에 있다. 앱을 먼저 한 번 띄운다
-- 다시 돌리면 이전 시드와 그 시드에 k6가 붙인 데이터만 지우고 새로 넣는다. 직접 넣은 개발 데이터는 남는다
--
-- id는 md5('<종류>-<번호>')::uuid로 정해진다. k6가 같은 식으로 id를 만들어 DB를 따로 조회하지 않는다
-- 사용자 비밀번호는 모두 loadtest1! (Argon2 해시를 앱에서 한 번 만들어 붙였다)

\if :{?users}
\else
  \set users 1000
\endif
\if :{?books}
\else
  \set books 3000
\endif
\if :{?reviews_per_user}
\else
  \set reviews_per_user 30
\endif
\if :{?comments}
\else
  \set comments 100000
\endif
\if :{?likes}
\else
  \set likes 200000
\endif

\timing on
BEGIN;

-- 지우기·넣기 동안 FK 검사를 끈다. FK 컬럼에 인덱스가 없어 행마다 검사하면 몇십 분 걸린다
-- 자식부터 지우고 넣으므로 정합성은 스크립트가 지킨다 (compose의 POSTGRES_USER는 슈퍼유저라 가능)
SET LOCAL session_replication_role = replica;

-- 1. 이전 시드 지우기 (시드 사용자·도서에 닿은 행 전부)
CREATE TEMP TABLE lt_users ON COMMIT DROP AS
  SELECT id FROM users WHERE email LIKE '%@loadtest.local';
CREATE TEMP TABLE lt_books ON COMMIT DROP AS
  SELECT id FROM books WHERE isbn LIKE '979000%';
CREATE TEMP TABLE lt_reviews ON COMMIT DROP AS
  SELECT id FROM reviews
  WHERE user_id IN (SELECT id FROM lt_users) OR book_id IN (SELECT id FROM lt_books);

DELETE FROM notifications
  WHERE user_id IN (SELECT id FROM lt_users) OR review_id IN (SELECT id FROM lt_reviews);
DELETE FROM review_likes
  WHERE user_id IN (SELECT id FROM lt_users) OR review_id IN (SELECT id FROM lt_reviews);
DELETE FROM comments
  WHERE user_id IN (SELECT id FROM lt_users) OR review_id IN (SELECT id FROM lt_reviews);
DELETE FROM reviews WHERE id IN (SELECT id FROM lt_reviews);
DELETE FROM books WHERE id IN (SELECT id FROM lt_books);
DELETE FROM users WHERE id IN (SELECT id FROM lt_users);

-- 2. 사용자
INSERT INTO users (id, created_at, updated_at, email, name, password)
SELECT md5('user-' || n)::uuid,
       now() - interval '730 days' + (n * interval '730 days' / :users),
       NULL,
       'user' || n || '@loadtest.local',
       '부하' || n,
       '$argon2id$v=19$m=19456,t=2,p=1$oMSipnSoRxXeiZYEuJNq5g$ZkoyVVwn9XR+CjZ+izmmqJrw15NJc+LxxrO7aYUoFzo'
FROM generate_series(1, :users) n;

-- 3. 도서. 제목·설명에 주제어를 섞어 키워드 검색이 실제로 걸리게 한다
--    ISBN은 979000 + 번호 6자리 + 체크 숫자. 979-0은 악보용(ISMN) 대역이라 실제 도서와 겹치지 않고,
--    앱의 Isbn 값 객체가 체크섬까지 검사하므로 체크 숫자를 계산해 붙인다
INSERT INTO books (id, created_at, updated_at, author, description, isbn, published_date,
                   publisher, rating, rating_sum, review_count, thumbnail_url, title)
SELECT md5('book-' || n)::uuid,
       now() - interval '700 days' + (n * interval '700 days' / :books),
       NULL,
       '저자' || (n % 300),
       w.topic || '을(를) 다루는 ' || w.kind || '. 부하 테스트용 설명 ' || n,
       isbn12 || ((10 - (SELECT sum(substr(isbn12, i, 1)::int * CASE WHEN i % 2 = 1 THEN 1 ELSE 3 END)
                         FROM generate_series(1, 12) i) % 10) % 10),
       date '2000-01-01' + (n * 7 % 9000),
       '출판사' || (n % 40),
       0, 0, 0, NULL,
       w.topic || ' ' || w.kind || ' ' || n
FROM generate_series(1, :books) n
CROSS JOIN LATERAL (
  SELECT (ARRAY['자바','스프링','데이터베이스','클라우드','알고리즘','소설','에세이','역사','과학','경제'])[n % 10 + 1] AS topic,
         (ARRAY['입문','실전','완벽 가이드','이야기','노트'])[n % 5 + 1] AS kind,
         '979000' || lpad(n::text, 6, '0') AS isbn12
) w;

-- 4. 리뷰. 사용자 u의 k번째 리뷰는 도서 (u*37 + k*101) % books 번. 101과 books가 서로소면
--    한 사용자 안에서 도서가 겹치지 않아 '1인 1도서 1리뷰'를 지킨다 (3000, 300, 20000 모두 서로소)
INSERT INTO reviews (id, created_at, updated_at, comment_count, content, like_count, rating,
                     book_id, user_id)
SELECT md5('review-' || ((k * :users) + u))::uuid,
       now() - interval '365 days' + (((k * :users) + u) * interval '365 days' / (:users * :reviews_per_user)),
       NULL,
       0,
       (ARRAY['자바','스프링','데이터베이스','클라우드','알고리즘','소설','에세이','역사','과학','경제'])[(u + k) % 10 + 1]
         || ' 쪽으로 읽어 볼 만한 책이었다. 리뷰 ' || ((k * :users) + u),
       0,
       (u * 7 + k * 3) % 5 + 1,
       md5('book-' || (((u * 37) + (k * 101)) % :books + 1))::uuid,
       md5('user-' || u)::uuid
FROM generate_series(1, :users) u
CROSS JOIN generate_series(0, :reviews_per_user - 1) k;

-- 5. 댓글. 리뷰 작성 시각 뒤에 달린다
INSERT INTO comments (id, created_at, updated_at, deleted_at, content, review_id, user_id)
SELECT md5('comment-' || n)::uuid,
       r.created_at + ((n % 1440) * interval '1 minute'),
       NULL, NULL,
       '댓글 ' || n || ': 저도 그렇게 생각해요.',
       r.id,
       md5('user-' || ((n * 31) % :users + 1))::uuid
FROM generate_series(1, :comments) n
JOIN reviews r ON r.id = md5('review-' || ((n * 7919) % (:users * :reviews_per_user) + 1))::uuid;

-- 6. 좋아요. 리뷰 r의 j번째 좋아요는 사용자 (r*17 + j*137) % users 번. 137과 users가 서로소라 겹치지 않는다
INSERT INTO review_likes (id, created_at, liked, review_id, user_id)
SELECT md5('like-' || n)::uuid,
       r.created_at + ((n % 720) * interval '1 minute'),
       true,
       r.id,
       md5('user-' || (((rn * 17) + (j * 137)) % :users + 1))::uuid
FROM generate_series(1, :likes) n
CROSS JOIN LATERAL (SELECT (n - 1) % (:users * :reviews_per_user) + 1 AS rn,
                           (n - 1) / (:users * :reviews_per_user) AS j) p
JOIN reviews r ON r.id = md5('review-' || rn)::uuid;

-- 7. 알림. 남의 리뷰에 단 댓글·좋아요마다 리뷰 작성자에게 하나씩. 모두 읽지 않음
--    (읽음 처리된 알림은 NotificationScheduler가 5분 뒤 지운다)
INSERT INTO notifications (id, created_at, updated_at, deleted_at, confirmed, confirmed_at,
                           message, review_content, type, review_id, user_id)
SELECT gen_random_uuid(), c.created_at, NULL::timestamptz, NULL::timestamptz, false, NULL::timestamptz,
       '[' || a.name || ']님이 나의 리뷰에 댓글을 남겼습니다.', r.content, 'COMMENT', r.id, r.user_id
FROM comments c
JOIN reviews r ON r.id = c.review_id
JOIN users a ON a.id = c.user_id
WHERE a.email LIKE '%@loadtest.local' AND c.user_id <> r.user_id
UNION ALL
SELECT gen_random_uuid(), l.created_at, NULL, NULL, false, NULL,
       '[' || a.name || ']님이 나의 리뷰를 좋아합니다.', r.content, 'LIKE', r.id, r.user_id
FROM review_likes l
JOIN reviews r ON r.id = l.review_id
JOIN users a ON a.id = l.user_id
WHERE a.email LIKE '%@loadtest.local' AND l.user_id <> r.user_id;

-- 8. 카운터를 실제 행 수에 맞춘다 (앱은 이 컬럼을 원자 UPDATE로 올리고 내린다)
UPDATE reviews r
SET like_count = coalesce(l.cnt, 0), comment_count = coalesce(c.cnt, 0)
FROM reviews r2
LEFT JOIN (SELECT review_id, count(*) cnt FROM review_likes WHERE liked GROUP BY review_id) l
  ON l.review_id = r2.id
LEFT JOIN (SELECT review_id, count(*) cnt FROM comments WHERE deleted_at IS NULL GROUP BY review_id) c
  ON c.review_id = r2.id
WHERE r.id = r2.id AND r2.user_id IN (SELECT id FROM users WHERE email LIKE '%@loadtest.local');

UPDATE books b
SET review_count = s.cnt, rating_sum = s.total, rating = s.total::double precision / s.cnt
FROM (SELECT book_id, count(*) cnt, sum(rating) total
      FROM reviews WHERE deleted_at IS NULL GROUP BY book_id) s
WHERE b.id = s.book_id AND b.isbn LIKE '979000%';

COMMIT;

-- 지운 행이 남긴 공간을 정리하고 통계를 새로 잡는다. 통계가 없으면 플래너가 엉뚱한 계획을 고른다
VACUUM ANALYZE users, books, reviews, comments, review_likes, notifications;

SELECT 'users' AS tbl, count(*) FROM users WHERE email LIKE '%@loadtest.local'
UNION ALL SELECT 'books', count(*) FROM books WHERE isbn LIKE '979000%'
UNION ALL SELECT 'reviews', count(*) FROM reviews
UNION ALL SELECT 'comments', count(*) FROM comments
UNION ALL SELECT 'review_likes', count(*) FROM review_likes
UNION ALL SELECT 'notifications', count(*) FROM notifications;
