# 부하 테스트 (M 규모)

운영과 같은 크기의 울타리 안에서 덕후감을 두드려 본다.
기준은 인프라 설계의 가정(동시 사용자 수십 명 이하, Fargate 0.5 vCPU / 1GB 태스크 1개, RDS db.t4g.micro)이다.
목표는 **평소 30명은 여유 있게, 100명 근처에서 어디가 먼저 꺾이는지 확인**하는 것이다.

| 파일 | 하는 일 |
|---|---|
| `seed.sh` | 시드를 넣는 스크립트. 없을 때만 넣고, `--force`면 다시 넣는다 |
| `seed.sql` | 시드 데이터. 사용자 1,000 · 도서 3,000 · 리뷰 30,000 · 댓글 100,000 · 좋아요 200,000 · 알림 약 30만 |
| `k6/scenario.js` | 사용자 흐름 부하. `PROFILE=smoke\|load\|stress\|soak` |
| `k6/race.js` | 동시성. 50명이 같은 리뷰에 동시에 좋아요·댓글 → 카운터가 50인지 |
| `k6/lib.js` | 공용 함수 (시드 id 계산, 요청 태그) |

## 0. 준비

- Docker 전체를 **4 CPU / 4GB**로 묶는다. 8GB 노트북 조건을 흉내 내는 울타리다.
  - Mac: Docker Desktop → Settings → Resources
  - Windows: `%UserProfile%\.wslconfig`에 아래를 적고 `wsl --shutdown` 뒤 Docker Desktop 재시작

    ```ini
    [wsl2]
    memory=4GB
    processors=4
    ```

- k6 설치: Mac은 `brew install k6`, Windows는 `winget install k6 --source winget`.
  설치하기 싫으면 아래 명령의 `k6 run ...`을 Docker로 바꿔도 된다(4GB 울타리 안에서 돌아 수치가 조금 나빠진다).

  ```bash
  docker run --rm -v "$PWD/loadtest/k6:/scripts:ro" -e BASE_URL=http://host.docker.internal:8080 \
    grafana/k6:2.3.0 run /scripts/scenario.js
  ```

  Windows Git Bash에서는 앞에 `MSYS_NO_PATHCONV=1`을 붙이고 `$PWD` 대신 `$(pwd -W)`를 쓴다.

## 1. 띄우기

`./gradlew bootRun`으로 띄운 앱이 있으면 먼저 끈다(8080이 겹친다). 그다음 앱도 컨테이너로 띄운다.

```bash
docker compose --profile monitoring --profile loadtest up -d --build
docker compose logs -f app    # "Started DeokhugamApplication" 이 보이면 Ctrl+C (0.5 vCPU라 1분쯤 걸린다)
```

- 처음이면 모니터링 설정(`bash monitoring/setup.sh`)과 시드(`bash loadtest/seed.sh`)를 한 번 넣는다 (자세한 건 [README_Monitoring.md](../README_Monitoring.md))
- 앱 컨테이너는 k6 때만 쓴다. 끝나면 `docker compose stop app`으로 끄고 bootRun으로 돌아간다. 재시작 정책이 없어 PC를 켜도 다시 뜨지 않는다
- 컨테이너 앱도 `host.docker.internal:8080`으로 열리므로 Prometheus 설정은 그대로 쓴다

| 컨테이너 | 울타리 | 이유 |
|---|---|---|
| app | `mem_limit: 1g`, `cpus: "0.5"` | 운영 태스크와 같게. 로컬에서만 멀쩡하고 운영에서 OOM 나는 걸 막는다 |
| postgres | `mem_limit: 768m` | 운영 RDS는 1GB. 4GB 안에 다 넣으려고 조금 줄였다 |
| elasticsearch | 힙 512m + `mem_limit: 1g` | 가장 많이 먹는 컨테이너 |
| kibana | `--max-old-space-size=768` | 512면 Kibana 9.5가 시작 중에 죽는다(exit 134). 768에서 RSS 약 870MB |

## 2. 시드 넣기

앱이 한 번 떠서 테이블을 만든 뒤에 넣는다(테이블은 ddl-auto가 만든다). 앱이 뜨는 중이면 테이블이 생길 때까지 기다린다.

```bash
bash loadtest/seed.sh            # 없을 때만
bash loadtest/seed.sh --force    # 지우고 다시 (seed.sql을 고친 뒤)
```

엔티티가 바뀌면 `seed.sql`의 INSERT 컬럼도 같이 고친다. 안 맞으면 오류로 멈추고, 한 트랜잭션이라 DB는 그대로다.

- 20초 안팎 걸린다. 끝에 테이블별 행 수가 나온다
- 다시 돌리면 이전 시드와 거기에 k6가 붙인 데이터만 지우고 새로 넣는다. 직접 넣은 개발 데이터는 남는다
- 시드 사용자는 `user1@loadtest.local` ~ `user1000@loadtest.local`, 비밀번호는 모두 `loadtest1!`
- id는 `md5('user-5')::uuid`처럼 정해진다. k6가 같은 식으로 id를 계산해서 DB를 조회하지 않는다

다른 규모로 넣으려면 `bash loadtest/seed.sh --force` 뒤에 `-v`를 붙인다. k6에도 같은 값을 `-e USERS=... -e BOOKS=... -e REVIEWS_PER_USER=...`로 넘긴다.

| 규모 | 붙일 값 |
|---|---|
| S | `-v users=100 -v books=300 -v reviews_per_user=10 -v comments=3000 -v likes=5000` |
| M (기본) | 없음 |
| L | `-v users=10000 -v books=20000 -v reviews_per_user=30 -v comments=1000000 -v likes=2000000` |

## 3. 돌리기

순서대로 돌린다. 앞 단계가 깨지면 뒤 단계는 의미가 없다.

```bash
k6 run loadtest/k6/scenario.js                    # smoke: 2명 1분. 스크립트와 헤더가 맞는지
k6 run -e PROFILE=load loadtest/k6/scenario.js    # 30명 10분. 평소 상태
k6 run -e PROFILE=stress loadtest/k6/scenario.js  # 10→50→100→150명. 꺾이는 지점
k6 run -e PROFILE=soak loadtest/k6/scenario.js    # 30명 1시간. 힙·디스크가 새는지
k6 run loadtest/k6/race.js                        # 동시성. 카운터가 50인지
```

- 요청 비율은 읽기 85 : 쓰기 15다(도서 검색 30, 리뷰 목록 25, 리뷰 상세+댓글 15, 알림 10, 상세 5 / 댓글 7, 좋아요 5, 리뷰 3)
- 카카오 ISBN 조회, OCR, S3 업로드는 넣지 않았다. 외부 한도와 비용이 든다
- 통과 기준: 실패율 < 1%, 읽기 p95 < 500ms, 쓰기 p95 < 1s. stress는 기준을 넘는 게 정상이고 **몇 명에서 넘는지**를 본다
- 부하를 거는 동안에는 `docker compose stop kibana`로 Kibana를 꺼 두면 메모리가 남는다. 로그는 ES에 계속 쌓이니 끝나고 `docker compose start kibana`로 켜서 본다

## 4. 볼 것

| 어디서 | 무엇을 |
|---|---|
| k6 결과 | 실패율, `kind:read`·`kind:write` p95 |
| Grafana "덕후감 앱" | API 상위 5의 p95, Hikari 대기, 힙, Tomcat 스레드 |
| Prometheus | API별 p95: `histogram_quantile(0.95, sum by (le, method, uri)(rate(http_server_requests_seconds_bucket{uri=~"/api.*"}[3m])))` |
| `docker stats` | app이 1GiB에 얼마나 붙는지 |
| `docker inspect $(docker compose ps -q app) --format '{{.State.OOMKilled}}'` | `true`면 컨테이너 메모리 초과로 죽은 것이다. 운영에서도 똑같이 죽는다 |

앱 메모리는 원래 빠듯하다. Dockerfile의 `-XX:MaxRAMPercentage=75` 때문에 1GB 안에서 힙이 약 768MB이고, 메타스페이스와 스레드를 더하면 1GB에 거의 닿는다.

## 5. 첫 결과 (2026-10-31, Windows PC, 4GB 울타리 없이)

**load (30명 10분).** 통과. 약 17 RPS, 요청 11,643건 실패 0, 읽기 p95 212ms, 쓰기 p95 194ms.
app 메모리 529MiB / 1GiB, OOMKilled 없음.

| API | smoke p95 (앱 막 뜬 직후) | load p95 (10분) |
|---|---|---|
| GET /api/reviews | 2,258 ms | 311 ms |
| GET /api/notifications | 1,342 ms | 220 ms |
| GET /api/books | 688 ms | 57 ms |
| GET /api/comments | 259 ms | 91 ms |
| GET /api/reviews/{id} | 148 ms | 42 ms |
| GET /api/books/{id} | 15 ms | 17 ms |

- smoke가 느렸던 건 0.5 vCPU에서 앱이 막 떠 JIT가 덜 데워진 탓으로 보인다. **smoke 수치로 판단하지 말고 load부터 본다**
- 운영도 배포 직후 1~2분은 이렇게 느릴 것이다
- load에서도 최대 응답이 20초인 요청이 있었다. p99는 584ms라 드물지만, GC 멈춤인지 Hikari 대기인지는 아직 보지 않았다
- 가장 느린 두 개(리뷰 목록, 알림 목록)는 DB에서 seq scan을 한다. FK 컬럼(`comments.review_id`, `notifications.user_id` 등)에 인덱스가 하나도 없다. 같은 쿼리를 DB에서 직접 돌리면 10~40ms라 지금은 버티지만, L 규모나 stress에서 먼저 꺾일 후보다
- 전 프로필에서 `org.hibernate.SQL: DEBUG`가 켜져 있어 요청마다 SQL 로그가 남는다. 로그 양이 커지는 원인이다

**race (50명 동시).** 요청 100건은 모두 성공했는데 카운터가 틀렸다.

| | 실제 행 | 카운터 |
|---|---|---|
| 좋아요 | 50 | `like_count` 6 |
| 댓글 | 50 | `comment_count` 0 |

- 좋아요: `Review.increaseLikeCount()`가 엔티티 필드를 `++`로 올린다. 동시 요청끼리 덮어써서 갱신이 사라진다. ERD 문서는 원자 UPDATE(`@Modifying`)로 하기로 했다
- 댓글: `CommentApplicationService`가 댓글을 만들 때 `increaseCommentCount()`를 부르지 않는다. 동시성과 상관없이 늘 0이다
