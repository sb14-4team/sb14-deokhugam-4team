# 로컬 모니터링 (Grafana · Kibana)

내 PC에서 앱을 띄우고, **Grafana(숫자)**와 **Kibana(로그)**로 바로 보는 방법입니다.
모니터링 설정은 `setup.sh`, 시드 데이터는 `loadtest/seed.sh`가 넣습니다. 각 설정이 무슨 API 호출인지 궁금하면 [API_GUIDE.md](API_GUIDE.md)를 봅니다.

## 준비물

- Docker Desktop 실행 중 (메모리 6GB 이상 할당 권장)
- Git Bash (아래 명령은 모두 Git Bash 기준)
- JDK 21
- 빈 포트: 5432, 9200, 5601, 9090, 3001, 8080

## 처음 한 번

**1. 설정 파일과 컨테이너**

```bash
cp .env.example .env
```
```bash
docker compose --profile monitoring up -d
```

**2. 모니터링 설정 넣기** — 앱 없이 돌려도 됩니다.

```bash
set -a; . ./.env; set +a; bash monitoring/setup.sh
```

- Elasticsearch가 뜰 때까지 기다렸다가 진행합니다(1~3분). 마지막에 `완료`가 나오면 끝입니다.
- 몇 번을 실행해도 결과가 같습니다. 이상하면 그냥 다시 실행합니다.
- 포트가 겹치면 `.env`의 `POSTGRES_PORT`, `GRAFANA_PORT`를 바꿉니다.

**3. 앱 띄우기 (다른 터미널)** — 앱이 떠야 DB 테이블이 생깁니다.

```bash
MANAGEMENT_METRICS_DISTRIBUTION_PERCENTILESHISTOGRAM_HTTP_SERVER_REQUESTS=true SERVER_TOMCAT_MBEANREGISTRY_ENABLED=true ./gradlew bootRun
```

**4. 시드 데이터 넣기** — 3의 터미널에 `Started DeokhugamApplication`이 보인 뒤 실행합니다.

```bash
bash loadtest/seed.sh
```

- Postgres에 **약 63만 행**을 넣습니다(20초 안팎). 시드 사용자는 `user1@loadtest.local` ~ `user1000@loadtest.local`, 비밀번호 `loadtest1!`입니다.
- **처음 한 번만** 들어가고, 이미 있으면 "이미 있음"으로 끝납니다.
- 모니터링 설정(`setup.sh`)과 나눈 이유: 모니터링 설정은 거의 바뀌지 않지만, 시드(`loadtest/seed.sql`)는 엔티티가 바뀔 때마다 같이 고쳐야 합니다.
- **엔티티(테이블)를 바꿨으면** `loadtest/seed.sql`을 맞춰 고친 뒤 `bash loadtest/seed.sh --force`로 다시 넣습니다. 이전 시드만 지우고 새로 넣으며, 직접 넣은 개발 데이터는 남습니다.
- `seed.sql`이 테이블과 안 맞으면 오류를 내고 멈춥니다. 한 트랜잭션이라 DB는 넣기 전 그대로입니다.

## 그다음부터 (매번)

**앱만 띄웁니다.** `setup.sh`와 `seed.sh`는 다시 돌리지 않아도 됩니다.

```bash
MANAGEMENT_METRICS_DISTRIBUTION_PERCENTILESHISTOGRAM_HTTP_SERVER_REQUESTS=true SERVER_TOMCAT_MBEANREGISTRY_ENABLED=true ./gradlew bootRun
```

- 앞의 두 환경변수가 없으면 Grafana의 p95 응답시간·스레드 패널이 비어 있습니다. 앱이 뜨고 1분쯤 지나면 그래프와 로그가 들어옵니다.
- **데이터는 계속 남습니다.** 앱은 `ddl-auto: update`라서 테이블을 지우지 않고 고칠 것만 고칩니다. 시드와 직접 넣은 데이터, 대시보드 설정은 모두 Docker 볼륨에 남습니다.
- **PC를 껐다 켜도 됩니다.** 컨테이너가 `restart: unless-stopped`라서 Docker Desktop이 켜지면 같이 다시 뜹니다. Docker Desktop을 로그인할 때 자동 실행하도록 해 두면 앱만 띄우면 됩니다. 자동 실행이 아니면 Docker Desktop을 켠 뒤 `docker compose --profile monitoring up -d`.
- 직접 `docker compose --profile monitoring stop`으로 끈 컨테이너는 PC를 켜도 꺼진 채로 있습니다. 다시 쓸 때 `up -d`.

## 부하 테스트(k6) 할 때만

앱을 운영과 같은 크기(0.5 vCPU / 1GB) 컨테이너로 띄웁니다. 평소에는 쓰지 않습니다(무겁고, 코드를 고칠 때마다 다시 빌드해야 합니다).
bootRun 앱을 먼저 끕니다(8080이 겹칩니다).

```bash
docker compose --profile monitoring --profile loadtest up -d --build
```

끝나면 앱 컨테이너만 끄고 다시 bootRun으로 돌아갑니다.

```bash
docker compose stop app
```

k6 실행 방법과 결과는 [loadtest/README.md](../loadtest/README.md)에 있습니다. 앱 컨테이너도 같은 Postgres를 쓰므로 시드 데이터를 그대로 씁니다.

## 로그인

| 화면 | 주소 | 아이디 | 비밀번호 | 로그인하면 |
|---|---|---|---|---|
| Grafana | http://localhost:3001 | `admin` | `.env`의 `GRAFANA_ADMIN_PASSWORD` (기본 `grafana-local`) | "덕후감 앱" 대시보드 |
| Kibana | http://localhost:5601 | `elastic` | `.env`의 `ELASTIC_PASSWORD` (기본 `elastic-local`) | "덕후감 로그" 대시보드 |

- 계정은 이 둘뿐입니다. 포트를 내 PC(127.0.0.1)에만 열기 때문에 각자 로컬에서 쓰는 데는 문제없습니다.
- Kibana는 컨테이너를 재시작하면 다시 로그인해야 합니다.
- Kibana에서 INFO 로그까지 보려면 ☰ → Discover, 데이터 뷰 **덕후감 로그**를 고릅니다. 대시보드는 WARN·ERROR만 보여 줍니다.

## 화면 보는 법

| 질문 | 어디서 |
|---|---|
| 지금 서비스가 정상인가? | Grafana 맨 위 "한눈에" 6개. 초록 정상, 노랑 주의, 빨강 문제 |
| 어느 API가 문제인가? | Grafana "API" 행 |
| 서버 자원이 부족한가? | Grafana "DB · JVM" 행 |
| 정확히 무슨 에러인가? | Kibana "최근 에러" (스택 트레이스 포함) |
| 그 요청은 무엇을 했나? | Kibana Discover에서 `http.request.id : "<값>"` |

Grafana 패널의 Kibana 링크를 누르면 같은 시간대의 "최근 에러"로 넘어갑니다.

## 끄기 · 초기화

- 끄기(데이터 유지): `docker compose --profile monitoring --profile loadtest down` — 컨테이너를 지워도 볼륨은 남아서 `up -d`하면 그대로 이어집니다
- `-v`를 붙이면 **Postgres 데이터(시드 포함)까지** 지워집니다. 다시 `setup.sh`와 `loadtest/seed.sh`를 돌리면 처음 상태로 돌아갑니다. 모니터링만 처음 상태로 되돌리는 방법은 [API_GUIDE.md](API_GUIDE.md#한-번에-하기-처음으로-되돌리기-끄기)에 있습니다.

## 막히면

| 증상 | 해결 |
|---|---|
| Grafana 포트 `bind: ... forbidden` | PC에 설치된 다른 Grafana가 씀 → `.env`의 `GRAFANA_PORT` 변경 |
| `setup.sh`가 401 | `.env`를 안 읽음 → `set -a; . ./.env; set +a` 후 다시 |
| Kibana에 로그가 없음 | 앱이 안 떠 있음, 또는 시간 범위가 짧음 → 오른쪽 위 시간을 Last 1 hour로 |
| Grafana 그래프가 비어 있음 | 앱이 8080에 안 떠 있음 → http://localhost:9090/targets 확인 |
| `seed.sh`가 "테이블이 아직 없다" | 앱이 안 떠 있었음 → bootRun 뒤 `seed.sh` 다시 |
| `seed.sh`가 `ERROR: column ... does not exist` 등 | 엔티티가 바뀌었는데 `seed.sql`이 그대로 → `seed.sql`을 고친 뒤 `--force` |
| 앱 컨테이너 `ports are not available ... 8080` | bootRun·IDE 앱이 8080을 씀 → 그쪽을 끄거나 `docker compose stop app` |
| Kibana가 `exit 134`로 꺼짐 | 힙 부족 → Docker Desktop 메모리를 늘린다 (compose는 768MB로 잡아 둠) |
