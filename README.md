# 덕후감

## 개발 환경

- Java 21, Spring Boot 4.1.1, Gradle Wrapper 9.8.1
- PostgreSQL 15, Spring Data JPA, Spring MVC, Validation, Lombok
- springdoc-openapi 3.1.1, Spotless 8.2.0 / Google Java Format 1.28.0
- Spring Batch, Actuator, Prometheus 의존성 포함
- Node.js 22와 npm: 커밋 메시지 검사에만 사용

IntelliJ의 Project SDK와 Gradle JVM을 모두 Java 21로 설정합니다.

Git 저장소 초기화 또는 clone 이후 다음을 실행합니다.

```sh
npm ci
git init -b main
npm run setup:hooks
```

## 로컬 실행

```sh
cp .env.example .env
docker compose up -d --wait
./gradlew bootRun
```

기본 프로필은 `dev`입니다. Spring Boot도 로컬 프로필에서 `.env`를 properties 형식으로 읽습니다.
예제 비밀번호는 로컬 개발 전용이며 `.env`는 Git에 포함하지 않습니다.
DB 포트가 이미 사용 중이면 `.env`의 `POSTGRES_PORT`를 변경합니다.
ISBN 도서 정보 조회는 카카오 책 검색 API를 쓰므로 `.env`의 `KAKAO_REST_API_KEY`에 카카오 developers REST API 키를 넣습니다.
이미지 ISBN 인식은 OCR Space API를 쓰므로 `.env`의 `OCR_SPACE_API_KEY`에 https://ocr.space/ocrapi/freekey 에서 받은 무료 키를 넣습니다.
도서 썸네일은 S3에 저장하므로 `.env`의 `AWS_REGION`, `AWS_S3_BUCKET`을 채우고, 앱을 띄우기 전에 AWS CLI 2.32 이상으로 `aws login`을 합니다(액세스 키는 쓰지 않습니다).

- 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- 상태 확인: http://localhost:8080/actuator/health

로컬 `ddl-auto=update`는 개발 편의를 위한 설정입니다. 배포 전에는 스키마 관리 방식과 배포 프로필을 별도로 구성해야 합니다.
Batch Job 자동 실행은 기본적으로 비활성화했으며, 기능 구현 시 실행 시점을 명시적으로 연결합니다.

## Git 규칙

초급 프로젝트와 같은 `main`(제출·배포), `develop`(통합), `feat/*`, `fix/*`

PR 생성·갱신 시 Java 21 컴파일·JAR 패키징·포맷 검사와 커밋 메시지 검사를 수행합니다.

```sh
# 포맷 자동 수정
./gradlew spotlessApply

# 포맷 검사 — 파일을 수정하지 않음
./gradlew spotlessCheck

# 현재 CI와 같은 컴파일·패키징·포맷 검사
./gradlew assemble spotlessCheck
```

- pre-commit: `spotlessCheck`로 Java 포맷 검사
- commit-msg: Conventional Commits 검사
- 허용 type: `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`
- 예: `chore: 프로젝트 초기 설정`
- PR 생성 시 Java 21 컴파일·포맷 검사와 커밋 메시지 검사 실행

Gradle Wrapper와 `.env.example`, 공통 설정은 커밋합니다.
개인 `.env`, IDE 설정, 빌드 결과물, 로그와 업로드 파일은 제외합니다.

```
- 프로젝트 링크: https://codeit.notion.site/OCR-ISBN-v2-3276fd228e8d8163bbf1eee99cdd302d
```
