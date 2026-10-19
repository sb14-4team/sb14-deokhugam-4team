#!/usr/bin/env bash
# 배포된 앱이 ALB 뒤에서 제대로 응답하는지 확인한다.
# 사용법: bash .github/scripts/smoke-test.sh http://<ALB 주소>
set -uo pipefail

base_url="${1:?앱 주소를 넘겨 주세요 (예: http://xxx.ap-southeast-2.elb.amazonaws.com)}"
base_url="${base_url%/}"
failed=0

# 1. 헬스 체크: ALB가 새 태스크를 대상으로 붙이는 데 시간이 걸릴 수 있어 최대 2분 기다린다
max_attempts=12
for attempt in $(seq 1 "$max_attempts"); do
  body=$(curl -s --max-time 10 "$base_url/actuator/health")
  if [[ "$body" == *'"status":"UP"'* ]]; then
    echo "✅ /actuator/health → UP"
    break
  fi
  if [ "$attempt" -eq "$max_attempts" ]; then
    echo "❌ /actuator/health 가 UP 이 아닙니다: ${body:-응답 없음}"
    exit 1
  fi
  echo "… 헬스 체크 대기 중 ($attempt/$max_attempts)"
  sleep 10
done

# 2. 관리용 엔드포인트는 ALB 리스너 규칙에서 403으로 막혀야 한다
for path in /actuator /actuator/info /actuator/env /actuator/prometheus; do
  code=$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 "$base_url$path")
  if [ "$code" = "403" ]; then
    echo "✅ $path → 403"
  else
    echo "❌ $path → $code (ALB에서 403으로 막혀야 합니다)"
    failed=1
  fi
done

exit "$failed"
