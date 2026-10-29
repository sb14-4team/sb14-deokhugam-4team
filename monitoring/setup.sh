#!/usr/bin/env bash
# 모니터링 스택(Elasticsearch·Kibana·Grafana)을 API로 설정한다.
# 모든 호출이 PUT이거나 덮어쓰기라서 여러 번 실행해도 결과가 같다.
#
#   docker compose --profile monitoring up -d
#   bash monitoring/setup.sh
#
# 주소를 바꾸면(ES_URL, KIBANA_URL, GRAFANA_URL) EC2 같은 다른 곳에도 그대로 쓴다.
# GRAFANA_TOKEN을 주면 admin 계정 대신 그 서비스 계정 토큰으로 Grafana를 설정한다.
set -euo pipefail

cd "$(dirname "$0")/.."
if [ -f .env ]; then set -a; . ./.env; set +a; fi

ES_URL=${ES_URL:-http://localhost:9200}
KIBANA_URL=${KIBANA_URL:-http://localhost:5601}
GRAFANA_URL=${GRAFANA_URL:-http://localhost:${GRAFANA_PORT:-3001}}
ELASTIC_PASSWORD=${ELASTIC_PASSWORD:-elastic-local}
KIBANA_SYSTEM_PASSWORD=${KIBANA_SYSTEM_PASSWORD:-kibana-local}
FILEBEAT_ES_PASSWORD=${FILEBEAT_ES_PASSWORD:-filebeat-local}
GRAFANA_ES_PASSWORD=${GRAFANA_ES_PASSWORD:-grafana-reader-local}
GRAFANA_ADMIN_PASSWORD=${GRAFANA_ADMIN_PASSWORD:-grafana-local}
GRAFANA_TOKEN=${GRAFANA_TOKEN:-}

# es METHOD PATH [curl 옵션...]
es() {
  local method=$1 path=$2; shift 2
  curl -sS --fail-with-body -u "elastic:$ELASTIC_PASSWORD" -X "$method" "$ES_URL$path" \
    -H 'Content-Type: application/json' "$@"
  echo
}

# Kibana는 쓰기 요청마다 kbn-xsrf 헤더를 요구한다
kb() {
  local method=$1 path=$2; shift 2
  curl -sS --fail-with-body -u "elastic:$ELASTIC_PASSWORD" -X "$method" "$KIBANA_URL$path" \
    -H 'Content-Type: application/json' -H 'kbn-xsrf: true' "$@"
  echo
}

gf() {
  local method=$1 path=$2; shift 2
  curl -sS --fail-with-body -X "$method" "$GRAFANA_URL$path" \
    -H 'Content-Type: application/json' -H "Authorization: Bearer $GRAFANA_TOKEN" "$@"
  echo
}

# wait_for 이름 명령... : 명령이 성공할 때까지 5초 간격으로 최대 5분 기다린다
wait_for() {
  local name=$1; shift
  echo "… $name 기다리는 중"
  for _ in $(seq 60); do
    if "$@" >/dev/null 2>&1; then return; fi
    sleep 5
  done
  echo "$name 이(가) 응답하지 않는다" >&2
  exit 1
}

step() { printf '\n== %s\n' "$1"; }

# ---------------------------------------------------------------- Elasticsearch
wait_for Elasticsearch curl -sf -u "elastic:$ELASTIC_PASSWORD" "$ES_URL/_cluster/health"

step "ES: Kibana가 ES에 붙을 kibana_system 비밀번호"
es POST /_security/user/kibana_system/_password -d "{\"password\":\"$KIBANA_SYSTEM_PASSWORD\"}"

step "ES: 로그 보관 정책 (하루마다 새 인덱스, 7일 뒤 삭제)"
es PUT /_ilm/policy/deokhugam-logs -d @- <<'JSON'
{
  "policy": {
    "phases": {
      "hot": { "actions": { "rollover": { "max_age": "1d", "max_primary_shard_size": "1gb" } } },
      "delete": { "min_age": "7d", "actions": { "delete": {} } }
    }
  }
}
JSON

step "ES: logback 한 줄을 필드로 나누는 인제스트 파이프라인"
# dev 패턴: %d [%thread] %-5level %logger{36} [requestId=.. | clientIp=.. | method=.. | uri=..] - %kvp %msg
# dev 외 패턴: 같은데 [요청 정보] 부분이 없다
es PUT /_ingest/pipeline/deokhugam-logs -d @- <<'JSON'
{
  "description": "logback 줄(날짜 [스레드] 레벨 로거 [요청 정보] - 메시지)을 필드로 나눈다",
  "processors": [
    { "dissect": {
        "description": "dev 패턴: 로거 뒤에 MDC 요청 정보가 붙는다",
        "field": "message",
        "pattern": "%{log_time} %{+log_time} [%{process.thread.name}] %{log.level->} %{log.logger} [requestId=%{http.request.id} | clientIp=%{client.ip} | method=%{http.request.method} | uri=%{url.path}] - %{log_message}",
        "append_separator": " ",
        "ignore_failure": true } },
    { "dissect": {
        "description": "요청 정보가 없는 패턴 (dev 외 프로필)",
        "if": "ctx.log_time == null",
        "field": "message",
        "pattern": "%{log_time} %{+log_time} [%{process.thread.name}] %{log.level->} %{log.logger} - %{log_message}",
        "append_separator": " ",
        "ignore_failure": true } },
    { "trim": { "field": "log.logger", "ignore_missing": true } },
    { "remove": {
        "description": "요청 밖에서 찍힌 로그는 요청 정보가 none이라 지운다",
        "if": "ctx.http?.request?.id == 'none'",
        "field": ["http", "client", "url"],
        "ignore_missing": true } },
    { "date": {
        "if": "ctx.log_time != null",
        "field": "log_time",
        "formats": ["yyyy-MM-dd HH:mm:ss.SSS"],
        "timezone": "Asia/Seoul",
        "on_failure": [{ "append": { "field": "tags", "value": "log_time_parse_failure" } }] } },
    { "set": { "if": "ctx.log_message != null", "field": "message", "copy_from": "log_message" } },
    { "trim": { "field": "message", "ignore_missing": true } },
    { "remove": { "field": ["log_time", "log_message"], "ignore_missing": true } }
  ]
}
JSON

step "ES: deokhugam-logs 데이터 스트림 템플릿 (정책·파이프라인·매핑 연결)"
es PUT /_index_template/deokhugam-logs -d @- <<'JSON'
{
  "index_patterns": ["deokhugam-logs*"],
  "data_stream": {},
  "priority": 500,
  "template": {
    "settings": {
      "number_of_replicas": 0,
      "index.lifecycle.name": "deokhugam-logs",
      "index.default_pipeline": "deokhugam-logs"
    },
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "message": { "type": "text" },
        "log": { "properties": { "level": { "type": "keyword" }, "logger": { "type": "keyword" } } },
        "process": { "properties": { "thread": { "properties": { "name": { "type": "keyword" } } } } },
        "http": { "properties": { "request": { "properties": { "id": { "type": "keyword" }, "method": { "type": "keyword" } } } } },
        "client": { "properties": { "ip": { "type": "keyword" } } },
        "url": { "properties": { "path": { "type": "keyword" } } }
      }
    }
  }
}
JSON

step "ES: 쓰기 전용(Filebeat)·읽기 전용(Grafana) 역할과 사용자"
es PUT /_security/role/deokhugam_logs_writer -d '{
  "cluster": ["monitor"],
  "indices": [{ "names": ["deokhugam-logs*"], "privileges": ["create_doc", "auto_configure", "view_index_metadata"] }]
}'
es PUT /_security/role/deokhugam_logs_reader -d '{
  "cluster": ["monitor"],
  "indices": [{ "names": ["deokhugam-logs*"], "privileges": ["read", "view_index_metadata"] }]
}'
es PUT /_security/user/filebeat_writer \
  -d "{\"password\":\"$FILEBEAT_ES_PASSWORD\",\"roles\":[\"deokhugam_logs_writer\"]}"
es PUT /_security/user/grafana_reader \
  -d "{\"password\":\"$GRAFANA_ES_PASSWORD\",\"roles\":[\"deokhugam_logs_reader\"]}"

# ---------------------------------------------------------------- Kibana
wait_for Kibana curl -sf -u "elastic:$ELASTIC_PASSWORD" "$KIBANA_URL/api/status"

step "Kibana: 로그 데이터 뷰 (기본으로 지정)"
kb POST /api/data_views/data_view -d @- <<'JSON'
{
  "data_view": {
    "id": "deokhugam-logs",
    "name": "덕후감 로그",
    "title": "deokhugam-logs*",
    "timeFieldName": "@timestamp"
  },
  "override": true
}
JSON
kb POST /api/data_views/default -d '{"data_view_id":"deokhugam-logs","force":true}'

# ---------------------------------------------------------------- Grafana
wait_for Grafana curl -sf "$GRAFANA_URL/api/health"

if [ -z "$GRAFANA_TOKEN" ]; then
  step "Grafana: 서비스 계정과 10분짜리 토큰 (admin 계정으로 한 번만)"
  ga() { curl -sS --fail-with-body -u "admin:$GRAFANA_ADMIN_PASSWORD" -H 'Content-Type: application/json' "$@"; }
  sa_id=$(ga "$GRAFANA_URL/api/serviceaccounts/search?query=deokhugam-setup" \
    | sed -n 's/.*"serviceAccounts":\[{"id":\([0-9]*\).*/\1/p')
  if [ -z "$sa_id" ]; then
    sa_id=$(ga -X POST "$GRAFANA_URL/api/serviceaccounts" -d '{"name":"deokhugam-setup","role":"Admin"}' \
      | sed -n 's/^{"id":\([0-9]*\).*/\1/p')
  fi
  GRAFANA_TOKEN=$(ga -X POST "$GRAFANA_URL/api/serviceaccounts/$sa_id/tokens" \
    -d "{\"name\":\"setup-$(date +%s)\",\"secondsToLive\":600}" \
    | sed -n 's/.*"key":"\([^"]*\)".*/\1/p')
  echo "서비스 계정 id=$sa_id, 토큰 발급 완료"
fi

# 있으면 PUT으로 덮어쓰고 없으면 POST로 만든다 (uid를 고정해 대시보드가 이 uid로 참조한다)
# 본문은 stdin으로 보낸다. Windows curl은 인자로 받은 한글을 시스템 코드페이지로 바꿔 깨뜨린다
upsert_datasource() {
  local uid=$1 body=$2 code
  code=$(curl -s -o /dev/null -w '%{http_code}' -H "Authorization: Bearer $GRAFANA_TOKEN" \
    "$GRAFANA_URL/api/datasources/uid/$uid")
  if [ "$code" = 200 ]; then
    printf '%s' "$body" | gf PUT "/api/datasources/uid/$uid" --data-binary @-
  else
    printf '%s' "$body" | gf POST /api/datasources --data-binary @-
  fi
}

step "Grafana: Prometheus 데이터 소스 (앱 메트릭)"
upsert_datasource deokhugam-prometheus '{
  "uid": "deokhugam-prometheus", "name": "Prometheus", "type": "prometheus",
  "access": "proxy", "url": "http://prometheus:9090", "isDefault": true
}'

step "Grafana: Elasticsearch 데이터 소스 (앱 로그, 읽기 전용 사용자)"
upsert_datasource deokhugam-es "{
  \"uid\": \"deokhugam-es\", \"name\": \"Elasticsearch 로그\", \"type\": \"elasticsearch\",
  \"access\": \"proxy\", \"url\": \"http://elasticsearch:9200\",
  \"basicAuth\": true, \"basicAuthUser\": \"grafana_reader\",
  \"secureJsonData\": { \"basicAuthPassword\": \"$GRAFANA_ES_PASSWORD\" },
  \"jsonData\": { \"index\": \"deokhugam-logs*\", \"timeField\": \"@timestamp\",
                  \"logMessageField\": \"message\", \"logLevelField\": \"log.level\" }
}"

step "Grafana: 데이터 소스 연결 확인"
gf GET /api/datasources/uid/deokhugam-prometheus/health
gf GET /api/datasources/uid/deokhugam-es/health

step "Grafana: 대시보드"
{ printf '{"dashboard": '; cat monitoring/grafana-dashboard.json; printf ', "overwrite": true}'; } \
  | gf POST /api/dashboards/db --data-binary @-

cat <<EOF

완료
  Kibana   $KIBANA_URL/app/discover   (elastic / ELASTIC_PASSWORD)
  Grafana  $GRAFANA_URL/d/deokhugam-app   (admin / GRAFANA_ADMIN_PASSWORD)
EOF
