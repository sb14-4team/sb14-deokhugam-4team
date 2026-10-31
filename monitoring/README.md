# 로컬 모니터링 (Grafana · Kibana)

내 PC에서 앱을 띄우고, **Grafana(숫자)**와 **Kibana(로그)**로 바로 보는 방법입니다.
설정은 `setup.sh`가 전부 자동으로 넣습니다. 각 설정이 무슨 API 호출인지 궁금하면 [API_GUIDE.md](API_GUIDE.md)를 봅니다.

## 준비물

- Docker Desktop 실행 중 (메모리 6GB 이상 할당 권장)
- Git Bash (아래 명령은 모두 Git Bash 기준)
- JDK 21
- 빈 포트: 5432, 9200, 5601, 9090, 3001, 8080

## 처음 한 번

```bash
cp .env.example .env
```
```bash
docker compose --profile monitoring up -d
```
```bash
set -a; . ./.env; set +a; bash monitoring/setup.sh
```

- `setup.sh`는 Elasticsearch가 뜰 때까지 기다렸다가 진행합니다(1~3분). 마지막에 `완료`가 나오면 끝입니다.
- 몇 번을 실행해도 결과가 같습니다. 이상하면 그냥 다시 실행합니다.
- 포트가 겹치면 `.env`의 `POSTGRES_PORT`, `GRAFANA_PORT`를 바꿉니다.

## 앱 띄우기 (매번)

```bash
MANAGEMENT_METRICS_DISTRIBUTION_PERCENTILESHISTOGRAM_HTTP_SERVER_REQUESTS=true SERVER_TOMCAT_MBEANREGISTRY_ENABLED=true ./gradlew bootRun
```

앞의 두 환경변수가 없으면 Grafana의 p95 응답시간·스레드 패널이 비어 있습니다. 앱이 뜨고 1분쯤 지나면 그래프와 로그가 들어옵니다.

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

- 끄기(데이터 유지): `docker compose --profile monitoring down`
- `-v`를 붙이면 **Postgres 데이터까지** 지워집니다. 모니터링만 처음 상태로 되돌리는 방법은 [API_GUIDE.md](API_GUIDE.md#한-번에-하기-처음으로-되돌리기-끄기)에 있습니다.

## 막히면

| 증상 | 해결 |
|---|---|
| Grafana 포트 `bind: ... forbidden` | PC에 설치된 다른 Grafana가 씀 → `.env`의 `GRAFANA_PORT` 변경 |
| `setup.sh`가 401 | `.env`를 안 읽음 → `set -a; . ./.env; set +a` 후 다시 |
| Kibana에 로그가 없음 | 앱이 안 떠 있음, 또는 시간 범위가 짧음 → 오른쪽 위 시간을 Last 1 hour로 |
| Grafana 그래프가 비어 있음 | 앱이 8080에 안 떠 있음 → http://localhost:9090/targets 확인 |
