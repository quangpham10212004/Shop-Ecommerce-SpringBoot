# Kế hoạch triển khai từng bước — ECommerce Platform

> Tài liệu này chia nhỏ SRD thành các bước tăng dần, từ nền tảng hiện có đến các tính năng nâng cao.
> Mỗi phase có thể deploy độc lập và không break phase trước.

---

## Trạng thái hiện tại (đã có)

| Service | Có gì | Còn thiếu gì đáng chú ý |
|---|---|---|
| `user-service` | CRUD user, search | Thiếu `tier`, `updatedAt`; chưa sync Keycloak |
| `product-service` | CRUD product, search cơ bản | Thiếu bảng `categories`; chưa có cache 2 tầng, chưa có Kafka |
| `order-service` | Tạo/hủy đơn, Feign gọi product | `status` enum chỉ có CREATED/CANCELLED; chưa có outbox |
| `api-gateway` | Routing qua Eureka | Chưa validate JWT, chưa rate limit |
| `discovery-server` | Eureka registry | Đủ dùng |

---

## Phase 0 — Dọn dẹp & chuẩn hóa nền tảng hiện có
> **Mục tiêu:** Code hiện tại sạch, nhất quán, chạy được end-to-end trước khi thêm bất cứ gì.
> **Không thêm dependency mới.** Không cần Kafka, Redis hay Keycloak ở phase này.

### Việc cần làm

#### user-service
- [ ] Thêm field `tier` (`NORMAL` / `VIP`) và `updatedAt` vào entity `User`
- [ ] Thêm `@PreUpdate` set `updatedAt` tự động
- [ ] Chuẩn hóa response về `UserResponse` DTO (tách khỏi entity)

#### product-service
- [ ] Tạo entity `Category` (`id`, `name`, `parentId`) + bảng `categories`
- [ ] Thêm `description`, `imageUrl`, `updatedAt` vào `Product`
- [ ] Chuyển getters/setters sang Lombok (đồng nhất với user-service)
- [ ] Chuẩn hóa `ProductResponse` DTO

#### order-service
- [ ] Mở rộng `OrderStatus`: thêm `CONFIRMED`, `PAYMENT_PENDING`, `STOCK_RESERVED`
- [ ] Thêm `updatedAt` vào `Order`
- [ ] Chuyển getters/setters sang Lombok

#### api-gateway
- [ ] Thêm route health check forward `/actuator/health` của từng service
- [ ] Thêm header `X-Request-Id` vào mọi request đi qua gateway

### DB thay đổi ở phase này

```sql
-- user-service
ALTER TABLE users ADD COLUMN tier VARCHAR(10) NOT NULL DEFAULT 'NORMAL';
ALTER TABLE users ADD COLUMN updated_at DATETIME;

-- product-service
CREATE TABLE categories (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    parent_id BIGINT NULL,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

ALTER TABLE products ADD COLUMN description TEXT;
ALTER TABLE products ADD COLUMN image_url VARCHAR(500);
ALTER TABLE products ADD COLUMN updated_at DATETIME;
ALTER TABLE products ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id);

-- order-service
ALTER TABLE orders ADD COLUMN updated_at DATETIME;
-- Nếu dùng ENUM MySQL:
ALTER TABLE orders MODIFY COLUMN status ENUM('CREATED','CONFIRMED','PAYMENT_PENDING','STOCK_RESERVED','CANCELLED') NOT NULL;
```

---

## Phase 1 — Auth & Gateway cơ bản
> **Mục tiêu:** Mọi request phải có JWT hợp lệ. Gateway là điểm kiểm soát duy nhất.
> **Thêm:** Keycloak (Docker), JWT filter trên Gateway.

### Việc cần làm

#### Keycloak setup
- [ ] Thêm Keycloak vào `docker-compose.yml` (port 8081)
- [ ] Tạo realm `ecommerce`, client `api-gateway`, roles: `ROLE_CUSTOMER`, `ROLE_VIP`, `ROLE_SELLER`, `ROLE_ADMIN`

#### api-gateway
- [ ] Thêm `spring-boot-starter-oauth2-resource-server` (WebFlux)
- [ ] Config `SecurityWebFilterChain` — validate JWT signature qua Keycloak JWK URI
- [ ] Sau validate, extract `userId` + `role` từ JWT claims → forward header `X-User-Id`, `X-User-Role`
- [ ] Public routes (không cần JWT): `POST /api/auth/login`, `POST /api/auth/register`, `GET /v1/products/**`

#### auth-service (mới — nhỏ)
- [ ] Tạo service mới port 8189
- [ ] `POST /api/auth/register` → gọi Keycloak Admin REST API tạo user
- [ ] `POST /api/auth/login` → gọi Keycloak token endpoint, trả JWT
- [ ] `POST /api/auth/refresh` → gọi Keycloak refresh

### Không thay đổi DB ở phase này

---

## Phase 2 — Caching 2 tầng cho Product
> **Mục tiêu:** Product reads không chạm DB trong phần lớn trường hợp.
> **Thêm:** Redis (Docker), Caffeine, Spring Cache.

### Việc cần làm

#### product-service
- [ ] Thêm Redis vào `docker-compose.yml` (port 6379)
- [ ] Thêm `spring-boot-starter-data-redis` + `caffeine` vào `pom.xml`
- [ ] Config `CacheManager`: L1 = Caffeine (TTL 5s, max 500), L2 = Redis (TTL 60s)
- [ ] Annotate `getProductById()` với `@Cacheable(cacheNames = "products")`
- [ ] Annotate `updateProduct()` với `@CacheEvict`
- [ ] Test: gọi GET product 3 lần liên tiếp → chỉ 1 lần hit DB

#### Không thay đổi DB ở phase này

---

## Phase 3 — Distributed Lock cho stock
> **Mục tiêu:** Không bị race condition khi nhiều request cùng giảm tồn kho.
> **Thêm:** Redisson.

### Việc cần làm

#### product-service
- [ ] Thêm `redisson-spring-boot-starter` vào `pom.xml`
- [ ] Implement `PUT /v1/products/{id}/lock` — acquire `RLock` với lease 5s, giảm stock, release lock
- [ ] Nếu không acquire được lock trong 500ms → trả 409 `STOCK_LOCKED`
- [ ] order-service khi tạo đơn → gọi endpoint lock này thay vì trừ thẳng stock

---

## Phase 4 — Kafka cơ bản + Cache Invalidation
> **Mục tiêu:** Services giao tiếp async qua Kafka. Cache product tự invalidate khi có update.
> **Thêm:** Kafka (Docker).

### Việc cần làm

#### Infra
- [ ] Thêm Kafka + Zookeeper vào `docker-compose.yml` (port 9092)
- [ ] Tạo topics: `product.updated`, `order.created`

#### product-service (producer)
- [ ] Thêm `spring-kafka` vào `pom.xml`
- [ ] Sau mỗi `updateProduct()` thành công → publish `product.updated` event (JSON: `{productId, timestamp}`)
- [ ] Thêm `KafkaListener` consume `product.updated` → `@CacheEvict` Redis + Caffeine

#### order-service (producer)
- [ ] Sau tạo order thành công → publish `order.created` event (JSON: `{orderId, userId, totalAmount, items[]}`)

#### Chưa làm ở phase này
- Consumer của order.created (để notification) → Phase 6
- Outbox pattern → Phase 5

---

## Phase 5 — Outbox Pattern
> **Mục tiêu:** Đảm bảo không mất event dù service crash ngay sau khi write DB.
> **Thêm:** Bảng `outbox_events`, scheduled poller (chưa cần Debezium).

> **Lý do chưa dùng Debezium ngay:** Debezium cần cấu hình binlog MySQL, Kafka Connect cluster — phức tạp cho môi trường dev. Poller đơn giản hơn và đủ dùng cho phase này.

### DB thay đổi

```sql
-- Chạy trên DB của order-service VÀ product-service
CREATE TABLE outbox_events (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_type VARCHAR(50)  NOT NULL,  -- 'ORDER', 'PRODUCT'
    aggregate_id   VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(100) NOT NULL,  -- 'order.created', 'product.updated'
    payload        JSON         NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING',  -- PENDING, PUBLISHED, FAILED
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at   DATETIME     NULL,
    INDEX idx_outbox_status (status),
    INDEX idx_outbox_created (created_at)
);
```

### Việc cần làm

#### order-service
- [ ] Trong method `createOrder()`: wrap trong 1 `@Transactional` — insert `orders` + insert `outbox_events` cùng transaction
- [ ] Xóa publish Kafka trực tiếp ở Phase 4 (chuyển sang poller)
- [ ] Tạo `OutboxPoller`: `@Scheduled(fixedDelay = 1000)` — query `status=PENDING`, publish Kafka, update `status=PUBLISHED`
- [ ] Scheduled job xóa records `status=PUBLISHED` và `created_at < NOW() - 24h`

#### product-service
- [ ] Làm tương tự cho `product.updated` event

---

## Phase 6 — Circuit Breaker + Retry
> **Mục tiêu:** order-service không bị cascade fail khi product-service chậm hoặc down.
> **Thêm:** Resilience4j.

### Việc cần làm

#### order-service
- [ ] Thêm `resilience4j-spring-boot3` vào `pom.xml`
- [ ] Wrap Feign client gọi product-service bằng `@CircuitBreaker(name = "productService", fallbackMethod = "...")`
- [ ] Config: `slidingWindowSize=10`, `failureRateThreshold=50`, `waitDurationInOpenState=30s`
- [ ] Fallback: trả lỗi rõ ràng `503 SERVICE_UNAVAILABLE` thay vì timeout vô tận
- [ ] Thêm `@Retry(name = "productService")` với maxAttempts=3, waitDuration=500ms

---

## Phase 7 — Rate Limiting tại Gateway
> **Mục tiêu:** Chống abuse và bảo vệ downstream services.
> **Dùng:** Redis (đã có từ Phase 2) + Spring Cloud Gateway RequestRateLimiter.

### Việc cần làm

#### api-gateway
- [ ] Config `RequestRateLimiter` filter trên các routes cần bảo vệ
- [ ] Default: 100 req/s/user với `KeyResolver` theo `X-User-Id` header
- [ ] Endpoint auth: 5 req/phút/IP (chống brute-force)
- [ ] Trả `429 Too Many Requests` với header `X-RateLimit-Remaining` và `Retry-After`

---

## Phase 8 — Idempotency cho POST endpoints
> **Mục tiêu:** Client retry không tạo duplicate order.

### Việc cần làm

#### order-service
- [ ] Đọc header `Idempotency-Key` trên `POST /v1/orders`
- [ ] Trước khi xử lý: `SETNX idem:{key}` với TTL 24h trên Redis
  - Nếu key đã tồn tại → trả cached response (200 + body cũ)
  - Nếu chưa có → xử lý bình thường, lưu response vào Redis key đó
- [ ] Làm tương tự cho flash-sale purchase (Phase 9)

---

## Những gì để sau (Phase 9+)

Sau khi Phase 0–8 stable, mới tiếp tục:

| Phase | Nội dung | Độ phức tạp |
|---|---|---|
| 9 | Flash-sale engine (Redis Lua, Bloom Filter) | Cao |
| 10 | Notification fan-out (notification-service, DLQ) | Cao |
| 11 | Saga choreography (state machine, compensating events) | Rất cao |
| 12 | Nâng cấp Outbox lên Debezium CDC | Trung bình |
| 13 | Elasticsearch search + Trending (Redis Sorted Set) | Trung bình |
| 14 | Analytics service (Kafka Streams, WebSocket) | Cao |
| 15 | OpenTelemetry full (Jaeger, Prometheus, Grafana) | Trung bình |

---

## Tóm tắt thứ tự ưu tiên

```
Phase 0  →  Phase 1  →  Phase 2  →  Phase 3
 (cleanup)   (auth)     (cache)     (lock)
    ↓
Phase 4  →  Phase 5  →  Phase 6  →  Phase 7  →  Phase 8
 (kafka)    (outbox)   (circuit)   (ratelimit) (idempotency)
    ↓
  Phase 9+ (flash-sale, notification, saga, search...)
```

Mỗi phase có thể review và test độc lập trước khi tiếp tục phase tiếp theo.
