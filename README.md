# DiggIndie-BE

인디 밴드·공연 추천 서비스 DiggIndie의 백엔드 서버입니다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| 언어·프레임워크 | Java 21, Spring Boot 3.5 (Web, Data JPA, Validation, Security, Mail) |
| 데이터베이스 | PostgreSQL + pgvector (임베딩 기반 추천), Redis, Redisson (분산 락) |
| 인증 | Spring Security + JWT, 소셜 로그인 (OAuth) |
| 외부 연동 | Spotify API, OpenAI API, AWS S3 |
| API 문서 | springdoc-openapi (Swagger UI) |
| 인프라 | Terraform 모듈 (VPC, RDS, EC2, ECS), Nginx, Docker Compose |
| 배포 | GitHub Actions: Gradle 빌드 → Docker 이미지 ECR 푸시 → EC2 배포 |

## 구조

- `src/main/java/ceos/diggindie/domain`: `band`, `concert`, `board`, `magazine`, `keyword`, `member`, `spotify`, `openai` 등 도메인별 패키지
- `src/main/java/ceos/diggindie/common`: 공통 응답·오류 코드, 보안·OAuth·Redisson·S3·Swagger 설정
- `infra/`: Terraform 모듈(`modules/vpc`, `rds`, `ec2_instance`, `ecs_cluster`, `ecs_service`)과 blue/green 환경 설정
- `nginx/`: API 서버 리버스 프록시 설정

## 로컬 실행

```bash
cp .env.example .env   # 값 채우기
docker compose up -d   # 로컬 Redis
./gradlew bootRun
```
