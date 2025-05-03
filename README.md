# ✅ Git 브랜치 및 브랜치별 체크리스트

| 브랜치명 | 설명 |
|----------|------|
| `main` | 최종 통합 브랜치 (기본 프로젝트 세팅 포함) |
| `01-entity-setup` | 엔티티 및 ERD 기반 JPA 매핑 설계 |
| `02-rest-api-01` | 기본 REST API 구현 (User, Category 등 1차) |
| `03-rest-api-02` | Product, Order, OrderItem 등 API 확장 구현 |
| `04-api-docs` | Swagger 문서화 및 Postman 컬렉션 내보내기 |
| `05-cache-redis` | Redis 캐싱 적용 실습 |
| `06-search-elasticsearch` | Elasticsearch 연동 및 검색 기능 구현 |
| `07-auth-jwt` | JWT 인증 시스템 구현 |
| `08-webhook-integration` | Webhook 알림 연동 |
| `09-test-integration` | 통합 테스트 자동화 |
| `10-docker-deploy` | Docker 기반 배포 실습 |
| `11-k8s-deploy` | Kubernetes 기반 무중단 배포 |
| `12-cicd-actions` | GitHub Actions 기반 CI/CD |
| `13-ai-semantic-search` | OpenAI + LangChain 연동 |
| `14-log-monitoring` | ELK + Grafana 기반 모니터링 구축 |

---

## 📋 브랜치별 체크리스트

### `01-entity-setup`
- [ ] 요구사항 기반 ERD 설계
- [ ] User, Product, Order, OrderItem, Category 엔티티 구현
- [ ] 연관관계 매핑 설정
- [ ] Repository 인터페이스 정의
- [ ] MapStruct 적용

### `02-rest-api-01`
- [ ] DTO 정의 및 매핑
- [ ] User/Category 서비스 및 컨트롤러 구현
- [ ] 공통 예외 처리 및 응답 구조 구성

### `03-rest-api-02`
- [ ] Product, Order, OrderItem CRUD API 구현
- [ ] 주문 생성 시 재고 차감 처리
- [ ] 사용자 주문 목록 조회 구현

### `04-api-docs`
- [ ] springdoc-openapi 설정
- [ ] Swagger UI로 API 확인
- [ ] Postman 컬렉션 내보내기

### `05-cache-redis`
- [ ] Redis Docker 구성
- [ ] @Cacheable, @CacheEvict 적용
- [ ] TTL 설정 및 키 설계

### `06-search-elasticsearch`
- [ ] Elasticsearch 설치 및 연동
- [ ] 상품 검색 API 구현
- [ ] Query DSL 또는 MatchQuery 적용

### `07-auth-jwt`
- [ ] Spring Security 설정
- [ ] 로그인 → 토큰 발급 API 구현
- [ ] 토큰 인증 필터 구성

### `08-webhook-integration`
- [ ] Slack 또는 Kakao Webhook 설정
- [ ] 알림 전송 서비스 구현

### `09-test-integration`
- [ ] RestAssured 기반 통합 테스트
- [ ] 인증, 캐시, 검색 테스트 포함
- [ ] Testcontainers 연동

### `10-docker-deploy`
- [ ] Dockerfile 작성 및 빌드
- [ ] docker-compose.yml로 DB, Redis, ES 구성

### `11-k8s-deploy`
- [ ] Kind 클러스터 구성
- [ ] Deployment/Service/Ingress 작성
- [ ] ConfigMap/Secret 분리

### `12-cicd-actions`
- [ ] GitHub Actions Workflow 작성
- [ ] 도커 빌드 및 푸시
- [ ] K8s 배포 자동화 연동

### `13-ai-semantic-search`
- [ ] OpenAI API 키 등록
- [ ] LangChain RAG 구성
- [ ] 의미기반 검색 API 구현

### `14-log-monitoring`
- [ ] Filebeat, Logstash, Kibana 구성
- [ ] Grafana + Prometheus 메트릭 수집
- [ ] Kibana 대시보드 구축

---
