# Software Requirements Document (SRD)
## Hệ thống Thương mại Điện tử Microservices — *ECommerce Platform*


## 1. Giới thiệu

### 1.1 Mục đích
Tài liệu này đặc tả yêu cầu phần mềm cho **ECommerce Platform** — nền tảng thương mại điện tử kiến trúc microservices, hỗ trợ giao dịch quy mô lớn (flash-sale triệu request/phút), event-driven, và high-availability. Tài liệu bao gồm cả các thành phần **đã có** trong hệ thống hiện tại và các **enhancement** được đề xuất.

### 1.2 Phạm vi
Sản phẩm cung cấp:
- Quản lý sản phẩm, đơn hàng, người dùng
- Xác thực/phân quyền tập trung qua Keycloak
- Flash-sale engine chống oversell ở quy mô triệu user
- Notification fan-out đa kênh (email/push/SMS)
- Search, recommendation, real-time analytics
- Reliability primitives: distributed lock, circuit breaker, idempotency, saga

**Ngoài phạm vi:** thanh toán thực (chỉ giả lập), logistics, CRM, ERP.

### 1.3 Định nghĩa & thuật ngữ
| Term | Definition |
|---|---|
| SRD | Software Requirements Document |
| GMV | Gross Merchandise Value |
| CDC | Change Data Capture |
| CQRS | Command Query Responsibility Segregation |
| DLQ | Dead Letter Queue |
| RPO / RTO | Recovery Point / Time Objective |
| TPS | Transactions Per Second |
| P99 | 99th percentile latency |

### 1.4 Tài liệu tham chiếu
- Spring Cloud Gateway docs
- Redis OSS + Redisson docs
- Apache Kafka 3.x docs
- Keycloak 24.x admin guide
- OpenTelemetry Specification 1.30+

---

## 2. Mô tả tổng quan

### 2.2 Các nhóm chức năng chính

| ID | Nhóm chức năng | Trạng thái |
|---|---|---|
| F1 | Product Management | ✅ Có |
| F2 | Order Management | ✅ Có |
| F3 | User Management | ✅ Có |
| F4 | Authentication & Authorization | ✅ Có |
| F5 | Service Discovery & API Gateway | ✅ Có |
| F6 | Distributed Caching | ✅ Có (Caffeine + Redis) |
| F7 | Distributed Locking | ✅ Có (Redisson) |
| F8 | Asynchronous Messaging | ✅ Có (Kafka) |
| F9 | **Flash-Sale Engine** | 🆕 Đề xuất |
| F10 | **Notification Fan-out** | 🆕 Đề xuất |
| F11 | **Saga Orchestration** | 🆕 Đề xuất |
| F12 | **Outbox + CDC** | 🆕 Đề xuất |
| F13 | **Search & Trending** | 🆕 Đề xuất |
| F14 | **Real-time Analytics Dashboard** | 🆕 Đề xuất |
| F15 | **Rate Limiting & Idempotency** | 🆕 Đề xuất |
| F16 | **Observability (Tracing/Metrics/Logs)** | ⚠️ Một phần (Logstash) |

### 2.3 Phân loại người dùng
| Persona | Mô tả | Quyền |
|---|---|---|
| **Guest** | Khách chưa đăng nhập | Browse, search, add to cart |
| **Customer** | Người mua | Order, flash-sale, history, notifications |
| **VIP Customer** | Tier cao | + Higher rate limit, early access flash-sale |
| **Seller / Operator** | Quản trị sản phẩm | Product CRUD, inventory |
| **Admin** | Quản trị hệ thống | Toàn quyền + dashboard analytics |

### 2.4 Ràng buộc & giả định
- Triển khai trên Kubernetes (hoặc Docker Compose cho dev)
- Java 17, Spring Boot 3.x, Spring Cloud 2023.x
- MySQL 8.x là DB chính; Redis 7.x; Kafka 3.x; Elasticsearch 8.x
- Keycloak là Identity Provider duy nhất
- Tất cả service stateless, scale ngang được

---

## 3. Kiến trúc tổng thể

### 3.1 Sơ đồ logic
```
                     ┌─────────────────────────────────────────┐
                     │            Client (Web / Mobile)        │
                     └──────────────────┬──────────────────────┘
                                        │ HTTPS + JWT
                                        ▼
                          ┌────────────────────────────┐
                          │   API Gateway (Spring CG)  │
                          │  - OAuth2 JWT validation   │
                          │  - Rate Limit (Redis Lua)  │
                          │  - Routing via Eureka      │
                          └─────┬──────────┬───────────┘
                                │          │
        ┌───────────────────────┘          └───────────────────────────┐
        ▼                                                              ▼
┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│  Order Service   │   │ Product Service  │   │  User Service    │   │  Notification    │
│  - Create order  │   │  - CRUD          │   │  - CRUD          │   │  - Email/Push/SMS│
│  - Flash-sale    │   │  - Stock lock    │   │  - Profile       │   │  - DLQ + retry   │
│  - Outbox        │   │  - Cache 2-tier  │   │                  │   │                  │
└────┬─────────────┘   └────┬─────────────┘   └────┬─────────────┘   └────┬─────────────┘
     │                      │                      │                      │
     └──────────────┬───────┴──────────────────────┴──────────────────────┘
                    ▼
          ┌────────────────────┐    ┌────────────────────┐    ┌────────────────────┐
          │  Kafka Cluster     │    │  Redis Cluster     │    │   MySQL (per-svc)  │
          │  - order.created   │    │  - cache           │    │   + Outbox tables  │
          │  - flash-sale.evt  │    │  - rate-limit      │    └────────────────────┘
          │  - notif.*         │    │  - stock counter   │
          │  - DLQ             │    │  - cart            │
          └────────┬───────────┘    └────────────────────┘
                   │                          ▲
                   ▼                          │ CDC (Debezium)
          ┌────────────────────┐              │
          │  Kafka Streams /   │──────────────┘
          │  Analytics Worker  │
          └────────┬───────────┘
                   ▼
          ┌────────────────────┐    ┌────────────────────┐
          │  Elasticsearch     │    │  WebSocket Push    │
          │  - Product search  │    │  Admin Dashboard   │
          └────────────────────┘    └────────────────────┘

   Cross-cutting: Eureka (discovery) | Keycloak (auth) | OpenTelemetry → Jaeger / Prometheus / Loki
```

### 3.2 Phân rã service
| Service | Ngôn ngữ | Port | DB | Phụ thuộc |
|---|---|---|---|---|
| api-gateway | Java/WebFlux | 8282 | — | Eureka, Keycloak, Redis |
| auth-service | Java | 8189 | — | Keycloak |
| user-service | Java | 8181 | MySQL | Eureka |
| product-service | Java | 8888 | MySQL | Redis, Redisson, Kafka, Eureka |
| order-service | Java | 8080 | MySQL | Kafka, Redis, Feign→Product, Eureka |
| notification-service | Java | 8090 | MySQL (outbox) | Kafka, SMTP, FCM, SMS gateway |
| analytics-service | Java | 8091 | — | Kafka Streams, Redis, WebSocket |
| search-indexer | Java | — | — | Kafka (CDC), Elasticsearch |
| eureka-server | Java | 8762 | — | — |

---

## 4. Yêu cầu chức năng (Functional Requirements)

### F1 — Product Management
| ID | Yêu cầu |
|---|---|
| F1.1 | Hệ thống cho phép Seller tạo/cập nhật/xóa sản phẩm qua `POST/PUT/DELETE /v1/products` |
| F1.2 | Hỗ trợ tìm kiếm sản phẩm cơ bản `POST /v1/products/search` với pagination |
| F1.3 | Cache sản phẩm 2 tầng: Caffeine (L1, 5s) + Redis (L2, 60s) |
| F1.4 | Cache invalidation tự động khi product update (publish `product.updated` event) |
| F1.5 | API `PUT /v1/products/lock` khóa tồn kho tạm thời (Redisson RLock, lease 5s) |

### F2 — Order Management
| ID | Yêu cầu |
|---|---|
| F2.1 | Tạo đơn hàng qua `POST /v1/orders` — validate stock qua Product Service |
| F2.2 | Lưu order vào MySQL trong transaction cùng với bảng `outbox_event` |
| F2.3 | Publish event `order.created` qua Debezium CDC (không publish trực tiếp) |
| F2.4 | Customer xem lịch sử đơn `GET /v1/orders?userId=` (read từ CQRS view) |
| F2.5 | Hủy đơn `POST /v1/orders/{id}/cancel` — trigger saga rollback stock |

### F3 — User Management
| ID | Yêu cầu |
|---|---|
| F3.1 | CRUD user `/v1/users` |
| F3.2 | Search user theo name/role/department `/v1/users/search` |
| F3.3 | Profile update đồng bộ với Keycloak |

### F4 — Authentication & Authorization
| ID | Yêu cầu |
|---|---|
| F4.1 | Đăng ký `/api/auth/register` — tạo user trên Keycloak realm |
| F4.2 | Đăng nhập `/api/auth/login` — trả JWT từ Keycloak |
| F4.3 | Refresh token endpoint |
| F4.4 | API Gateway validate JWT (issuer, audience, expiration, signature) |
| F4.5 | Role-based access control: `ROLE_CUSTOMER`, `ROLE_VIP`, `ROLE_SELLER`, `ROLE_ADMIN` |
| F4.6 | OTP / Magic-link login (Redis TTL 60s, max 3 lần/phút) |

### F5 — API Gateway & Service Discovery
| ID | Yêu cầu |
|---|---|
| F5.1 | Tất cả service đăng ký với Eureka khi khởi động |
| F5.2 | Gateway định tuyến `lb://service-name` qua Eureka |
| F5.3 | Gateway thêm header `X-Trace-Id`, `X-User-Id` cho downstream |
| F5.4 | Health check `/actuator/health` cho từng service |

### F9 — Flash-Sale Engine 🆕
**User Story:** *Là một khách hàng, tôi muốn tham gia flash-sale để mua sản phẩm giá rẻ; hệ thống phải đảm bảo không bán quá số lượng và không bị nghẽn dù có 1 triệu user cùng truy cập.*

| ID | Yêu cầu |
|---|---|
| F9.1 | Admin tạo flash-sale campaign: `productId`, `stock`, `startAt`, `endAt`, `pricePromo`, `maxPerUser` |
| F9.2 | Khi `startAt`, hệ thống warm-up: pre-load stock vào Redis key `flashsale:{id}:stock` |
| F9.3 | User gọi `POST /v1/flashsale/{id}/purchase` → Lua script atomic: (a) check user quota, (b) DECR stock, (c) push `flashsale:queue` |
| F9.4 | Nếu `stock <= 0` → trả 409 `SOLD_OUT` ngay tại Redis (không chạm DB) |
| F9.5 | Worker consume queue → tạo order qua Order Service (async, batched) |
| F9.6 | Bloom Filter (Redisson) chặn user đã mua → giảm tải Redis chính |
| F9.7 | Rate limit 5 req/s/user, 1000 req/s/IP tại Gateway |
| F9.8 | Sau `endAt`, snapshot Redis → MySQL (final reconciliation) |

**Acceptance:**
- Không bao giờ oversell (0 dòng order vượt stock ban đầu) trong 100 lần load test
- P99 latency < 200ms tại endpoint purchase
- Throughput ≥ 50.000 TPS trên 1 partition Redis

### F10 — Notification Fan-out 🆕
**User Story:** *Khi flash-sale bắt đầu, hệ thống gửi thông báo cho 10 triệu user qua email/push/SMS trong vòng 5 phút.*

| ID | Yêu cầu |
|---|---|
| F10.1 | Admin tạo campaign: target audience (segment), channels, template, scheduledAt |
| F10.2 | Scheduler trigger → query user IDs theo segment, chia batch 10K, publish lên Kafka topic `notif.campaign` (50 partitions) |
| F10.3 | Mỗi channel có consumer group riêng: `email-worker`, `push-worker`, `sms-worker` |
| F10.4 | Idempotency: Redis `SETNX notif:{campaign}:{user}:{channel}` TTL 7 ngày |
| F10.5 | Retry với exponential backoff (1s, 5s, 30s, 5m); sau 4 lần fail → DLQ |
| F10.6 | DLQ có UI replay cho admin |
| F10.7 | Mỗi notification ghi audit log (vào ES) với delivery status |

**Acceptance:**
- 10M notification gửi xong trong < 5 phút
- Delivery rate ≥ 99.5%; duplicate < 0.01%

### F11 — Saga Pattern (Choreography) 🆕
| ID | Yêu cầu |
|---|---|
| F11.1 | Flow: `OrderCreated` → `StockReserved` → `PaymentCompleted` → `OrderConfirmed` |
| F11.2 | Compensating: `PaymentFailed` → `StockReleased` → `OrderCancelled` |
| F11.3 | Mỗi service tự xử lý state machine, không có orchestrator tập trung |
| F11.4 | Event lưu trong topic riêng với retention 7 ngày để replay |

### F12 — Outbox Pattern + CDC 🆕
| ID | Yêu cầu |
|---|---|
| F12.1 | Mỗi service có bảng `outbox_event(id, aggregate_type, aggregate_id, event_type, payload, created_at)` |
| F12.2 | Business write + outbox write nằm trong 1 DB transaction |
| F12.3 | Debezium connector đọc binlog → publish lên Kafka |
| F12.4 | Sau khi gửi thành công, scheduled job xóa outbox records cũ > 24h |

### F13 — Search & Trending 🆕
| ID | Yêu cầu |
|---|---|
| F13.1 | Debezium → Kafka → indexer → Elasticsearch (sản phẩm) |
| F13.2 | `GET /v1/products/search?q=&filters=` query ES, có Caffeine cache top queries |
| F13.3 | Mỗi view/buy ZINCRBY `trending:hourly` (Redis Sorted Set) |
| F13.4 | `GET /v1/products/trending?window=1h\|24h\|7d` lấy top 100 |

### F14 — Real-time Analytics Dashboard 🆕
| ID | Yêu cầu |
|---|---|
| F14.1 | Kafka Streams aggregate `order.created` → metric: orders/sec, GMV, top product |
| F14.2 | Push metric qua WebSocket lên admin dashboard |
| F14.3 | Lưu rollup 1min/1hour/1day vào Redis cho query lịch sử |

### F15 — Rate Limiting & Idempotency 🆕
| ID | Yêu cầu |
|---|---|
| F15.1 | Token bucket Redis + Lua tại Gateway, config theo route + tier user |
| F15.2 | Trả 429 với header `X-RateLimit-Remaining`, `Retry-After` |
| F15.3 | Idempotency-Key header trên POST endpoints (orders, payment, flash-sale) |
| F15.4 | Redis `SETNX idem:{key}` TTL 24h lưu response cached |

### F16 — Observability 🆕
| ID | Yêu cầu |
|---|---|
| F16.1 | OpenTelemetry instrumentation cho mọi service |
| F16.2 | Trace ID propagate qua HTTP header và Kafka header |
| F16.3 | Logs ghi MDC `traceId`, `spanId`, `userId` → Logstash → Elasticsearch (Loki optional) |
| F16.4 | Metrics export Prometheus; dashboard Grafana |
| F16.5 | Distributed trace UI qua Jaeger |
| F16.6 | Alert rule: error rate > 1%, P99 latency > 1s, Kafka lag > 10K |

---

## 5. Yêu cầu phi chức năng (Non-Functional Requirements)

### 5.1 Hiệu năng (Performance)
| Metric | Target |
|---|---|
| API P99 latency (đọc cached) | < 50 ms |
| API P99 latency (write) | < 300 ms |
| Flash-sale purchase P99 | < 200 ms |
| Notification fan-out throughput | ≥ 30K msg/s |
| Order creation throughput | ≥ 10K TPS |

### 5.2 Khả năng mở rộng (Scalability)
- Service stateless, scale ngang qua Kubernetes HPA (CPU/Memory/custom metric Kafka lag)
- Redis Cluster mode (≥ 6 nodes, replication factor 1)
- Kafka cluster ≥ 3 brokers, replication factor 3, min.insync.replicas=2
- MySQL: master-replica + read replicas; partition theo `user_id` hoặc `order_date` khi cần

### 5.3 Độ tin cậy (Reliability)
| Metric | Target |
|---|---|
| Availability | 99.9% (cho phép ~43 min downtime/tháng) |
| RPO | ≤ 1 phút |
| RTO | ≤ 15 phút |
| Data durability | Kafka acks=all + RF=3; MySQL binlog backup hourly |
| Circuit breaker | Resilience4j với 50% error rate trong 10s → open 30s |
| Retry policy | Exponential backoff, max 3 retry cho idempotent operation |

### 5.4 Bảo mật (Security)
- TLS 1.3 cho mọi external traffic
- mTLS giữa các internal service (optional với Istio)
- JWT validation tại Gateway + per-service double-check
- Secrets quản lý qua Vault / Kubernetes Secrets, không bao giờ trong code
- OWASP Top 10 mitigation: SQL injection (prepared statement), XSS (output encoding), CSRF (token), SSRF (URL whitelist)
- Audit log mọi action ADMIN-level, lưu 1 năm
- Rate limit chống brute-force login (5 lần / 15 phút / IP)
- PII encryption at rest (AES-256-GCM)

### 5.5 Khả năng quan sát (Observability)
- 100% service emit metrics, logs, traces theo OpenTelemetry
- Centralized logging (ELK / Loki)
- SLO dashboard cho từng service
- Synthetic monitoring cho critical user journey

### 5.6 Khả năng bảo trì (Maintainability)
- Code coverage ≥ 70% (unit) + ≥ 40% (integration)
- API có OpenAPI 3.0 spec
- Mỗi service có CI pipeline: build → test → SAST → container scan → deploy
- ADR (Architecture Decision Record) cho mọi quyết định lớn

### 5.7 Khả năng tương thích (Compatibility)
- Backward-compatible API versioning (`/v1`, `/v2`)
- Event schema có Schema Registry (Confluent / Apicurio), forward & backward compatible

### 5.8 Tuân thủ (Compliance)
- GDPR: user có quyền export & xóa dữ liệu (`/v1/users/me/data`, `/v1/users/me`)
- PCI-DSS: không lưu CVV; số thẻ tokenize qua payment gateway

---

## 6. Yêu cầu về dữ liệu

### 6.1 Mô hình dữ liệu chính
```
User (id, username, email, phone, role, tier, createdAt)
  └─ Order (id, userId, status, total, createdAt)
        └─ OrderItem (id, orderId, productId, quantity, price)

Product (id, name, sku, price, stock, categoryId, version)
Category (id, name, parentId)

FlashSaleCampaign (id, productId, startAt, endAt, stock, pricePromo, maxPerUser)
FlashSalePurchase (id, campaignId, userId, orderId, createdAt)

NotificationCampaign (id, name, segment, channels, template, scheduledAt, status)
NotificationLog (id, campaignId, userId, channel, status, sentAt, error)

OutboxEvent (id, aggregateType, aggregateId, eventType, payload, status, createdAt)
```

### 6.2 Phân tầng lưu trữ
| Loại data | Storage | Lý do |
|---|---|---|
| Transactional (Order, User, Product) | MySQL | ACID, relational |
| Cache (product detail, search result) | Redis + Caffeine | Low latency |
| Counter (flash-sale stock) | Redis | Atomic ops |
| Distributed lock | Redis + Redisson | Native support |
| Event log | Kafka (retention 7d) | Replay, decoupling |
| Search index | Elasticsearch | Full-text, faceting |
| Trending / leaderboard | Redis Sorted Set | O(log N) ranking |
| Trace & log | Elasticsearch + Loki | Aggregation |
| Metric | Prometheus | Time-series |

### 6.3 Data retention
| Loại | Thời gian |
|---|---|
| Order | 7 năm (compliance) |
| Notification log | 90 ngày |
| Kafka event | 7 ngày (hot) + S3 cold (1 năm) |
| Outbox | 24 giờ sau khi published |
| Trace | 14 ngày |
| Audit log | 1 năm |

---

## 7. Yêu cầu giao diện ngoài

### 7.1 API
- RESTful JSON; mã hóa UTF-8
- Versioning qua URL path (`/v1`, `/v2`)
- Pagination dạng cursor cho list endpoint lớn
- Error response chuẩn RFC 7807 Problem Details

### 7.2 Tích hợp bên ngoài
| Hệ thống | Mục đích | Giao thức |
|---|---|---|
| Keycloak | IdP | OIDC / REST |
| SMTP (SendGrid/AWS SES) | Email | SMTP / API |
| FCM / APNS | Push notification | HTTP/2 |
| Twilio / Viettel SMS | SMS | REST |
| Payment Gateway (giả lập) | Charge | REST + Webhook |
| S3 / MinIO | Product image, cold storage | S3 API |

---

## 8. Use case tiêu biểu (chi tiết)

### 8.1 UC-FS-01: Tham gia Flash-Sale
**Actor:** Customer (đã login)
**Pre-condition:** Campaign đang active, user chưa mua đủ quota
**Flow:**
1. User mở trang flash-sale, FE poll `/v1/flashsale/{id}/status`
2. Tới giờ start, FE enable nút "Mua"
3. User click → FE gọi `POST /v1/flashsale/{id}/purchase` với `Idempotency-Key`
4. Gateway check rate-limit, validate JWT, thêm trace ID
5. Order Service forward → Product Service chạy Lua script:
   - Check bloom filter `flashsale:{id}:users` → reject nếu đã có
   - DECR `flashsale:{id}:stock` → nếu < 0 → revert + return SOLD_OUT
   - LPUSH user vào `flashsale:{id}:winners`
6. Trả 202 Accepted với `purchaseId`
7. Worker pull `flashsale:{id}:winners`, gọi Order Service tạo order thật → write DB + outbox
8. Debezium publish `order.created` → Notification Service gửi email xác nhận
**Exception:**
- Stock = 0 → return 409 `SOLD_OUT`
- Đã mua → 409 `ALREADY_PURCHASED`
- Rate limit → 429
- Service down → Resilience4j circuit open → 503 với Retry-After

### 8.2 UC-NF-01: Gửi notification campaign cho 10M user
**Actor:** Admin
**Flow:**
1. Admin tạo campaign UI → POST `/v1/notif/campaigns`
2. Scheduled job kích hoạt tại `scheduledAt`
3. Producer query user IDs theo segment, batch 10K → publish lên `notif.campaign` (key=userId để giữ order per user)
4. 3 consumer group (email/push/sms) tiêu thụ song song
5. Worker lookup template, render, gọi provider (SMTP/FCM/SMS API)
6. Trên success → ghi `NotificationLog`, set Redis `notif:{campaign}:{user}:{channel}=1`
7. Trên fail → retry với backoff, sau 4 lần → DLQ `notif.dlq`
8. Admin dashboard hiển thị tiến độ real-time qua WebSocket

---

## 9. Roadmap triển khai

| Phase | Thời gian | Nội dung |
|---|---|---|
| **P0 - Foundation** | Đã có | Order, Product, User, Auth, Gateway, Eureka, Cache, Kafka cơ bản |
| **P1 - Reliability** | 4 tuần | Outbox + CDC, Saga, Circuit Breaker, Idempotency, Rate Limit |
| **P2 - Scale Event** | 4 tuần | Flash-sale engine, Notification fan-out, DLQ |
| **P3 - Intelligence** | 3 tuần | Elasticsearch search, Trending, Recommendation cơ bản |
| **P4 - Observability** | 2 tuần | OpenTelemetry full, Grafana dashboard, SLO alerting |
| **P5 - Hardening** | ongoing | Chaos engineering, load test định kỳ, security audit |

---

## 10. Rủi ro & giảm thiểu

| Rủi ro | Tác động | Giảm thiểu |
|---|---|---|
| Redis single point of failure cho flash-sale | Mất doanh thu | Redis Cluster + Sentinel, replication, snapshot/AOF |
| Kafka consumer lag bùng nổ | Notification chậm | Auto-scale consumer theo lag metric, partition đủ nhiều |
| Oversell do race condition | Mất uy tín | Lua atomic + reconciliation job cuối campaign |
| DB hot row (stock) | Bottleneck | Toàn bộ stock check ở Redis, DB chỉ snapshot |
| Cache stampede | DB sập | Redisson mutex khi miss, refresh-ahead |
| JWT key leak | Bảo mật | Rotate key định kỳ qua Keycloak, short TTL |
| Spam đăng nhập | Account compromise | Rate limit + CAPTCHA + account lock |

---

## 11. Phụ lục

### 11.1 Mapping repo hiện tại → service trong SRD
| Hiện tại | SRD service |
|---|---|
| `vtidtn2506_order_service` | order-service |
| `vtidtn2506_product_service` | product-service |
| `vtidtn2508-order-service/demo` | order-service (alternative impl) |
| `vtidtn2508-order-service/vtibackend-dtn2509-user-mng` | user-service |
| `vtidtn2508-order-service/vtibackend-dtn2508-auth-service` | auth-service |
| `vtidtn2508-order-service/vtibackend-dtn2508-api-gateway` | api-gateway |
| `vtidtn2508-order-service/vtibackend-dtn2508-service-registry` | eureka-server |
| `vtidtn2506_api_gateway` | api-gateway (alt) |

### 11.2 Bộ công nghệ tổng hợp
- **Backend:** Java 17, Spring Boot 3.x, Spring Cloud Gateway, Spring WebFlux, Spring Data JPA, Spring Kafka
- **Messaging:** Apache Kafka 3.x, Debezium 2.x
- **Cache & Lock:** Redis 7.x (Cluster), Redisson, Caffeine
- **Search:** Elasticsearch 8.x
- **Auth:** Keycloak 24.x, OAuth2/OIDC, JWT
- **Discovery:** Netflix Eureka
- **Resilience:** Resilience4j (CB, Retry, Bulkhead, RateLimiter)
- **Observability:** OpenTelemetry, Jaeger, Prometheus, Grafana, Logstash, Loki
- **CI/CD:** GitHub Actions / GitLab CI, Docker, Helm, ArgoCD
- **Infra:** Kubernetes, MySQL 8 (master-replica), MinIO/S3
---
