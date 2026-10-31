#!/usr/bin/env bash
# 로컬 Postgres(이 compose의 postgres 컨테이너)에 시드 데이터(loadtest/seed.sql)를 넣는다.
# monitoring/setup.sh와 나눈 이유: 모니터링 설정은 거의 바뀌지 않지만, 시드는 엔티티(테이블)가
# 바뀔 때마다 seed.sql을 같이 고쳐야 한다. 바뀌는 축이 달라서 파일도 나눴다.
#
#   bash loadtest/seed.sh                 # 시드가 없을 때만 넣는다 (처음 한 번)
#   bash loadtest/seed.sh --force         # 지우고 다시 넣는다 (seed.sql을 고친 뒤)
#   bash loadtest/seed.sh --force -v users=100 -v books=300 -v reviews_per_user=10 \
#     -v comments=3000 -v likes=5000      # 규모 바꾸기 (-v는 그대로 psql에 넘긴다)
#
# 테이블은 앱이 ddl-auto로 만든다. 앱(bootRun)을 한 번 띄운 뒤 실행한다.
# seed.sql은 한 트랜잭션이라 중간에 실패하면(컬럼이 바뀌었는데 안 고친 경우 등) DB는 넣기 전 그대로다.
set -euo pipefail

cd "$(dirname "$0")/.."
if [ -f .env ]; then set -a; . ./.env; set +a; fi

APP_URL=${APP_URL:-http://localhost:8080}
force=false
if [ "${1:-}" = --force ]; then force=true; shift; fi

psql_local() {
  docker compose exec -T postgres psql -U "${POSTGRES_USER:-deokhugam}" -d "${POSTGRES_DB:-deokhugam}" "$@"
}

# seed.sql이 넣는 테이블 6개가 다 있어야 한다
tables_ready() {
  [ "$(psql_local -Atc "select count(*) from information_schema.tables where table_schema = 'public'
    and table_name in ('users','books','reviews','comments','review_likes','notifications')")" = 6 ]
}

if [ -z "$(docker compose ps -q postgres 2>/dev/null)" ]; then
  echo "이 compose의 postgres가 떠 있지 않다. docker compose up -d 먼저" >&2
  exit 1
fi

# 앱(bootRun·IDE·k6용 컨테이너)이 떠 있거나 뜨는 중이면 테이블이 생길 때까지 최대 5분 기다린다
if ! tables_ready && { [ -n "$(docker compose ps -q app 2>/dev/null)" ] \
    || curl -s -o /dev/null --max-time 3 "$APP_URL/actuator/health"; }; then
  echo "… 앱이 테이블을 만들 때까지 기다리는 중"
  for _ in $(seq 60); do tables_ready && break; sleep 5; done
fi
if ! tables_ready; then
  echo "테이블이 아직 없다. 앱(bootRun)을 한 번 띄운 뒤 다시 실행한다" >&2
  exit 1
fi

seeded=$(psql_local -Atc "select count(*) from users where email like '%@loadtest.local'")
if [ "$force" = false ] && [ "$seeded" -gt 0 ]; then
  echo "이미 있음 (시드 사용자 ${seeded}명). 다시 넣으려면 --force"
  exit 0
fi

echo "… 시드를 넣는 중 (M 규모 20초 안팎)"
psql_local -q -v ON_ERROR_STOP=1 "$@" < loadtest/seed.sql | grep -vE '^Time: '
