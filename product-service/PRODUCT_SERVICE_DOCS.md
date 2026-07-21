# Product Service

Microservice quản lý **Product** và **Category** trong hệ thống E-Commerce.

## Tech Stack

| Component       | Technology                        |
|-----------------|-----------------------------------|
| Framework       | Spring Boot 3.x                   |
| ORM             | Hibernate / Spring Data JPA       |
| Mapper          | MapStruct                         |
| Database        | MariaDB (port 3306)               |
| DB Name         | `product_service_db`              |
| Service Discovery | Eureka (`localhost:8762`)       |
| Server Port     | `8888`                            |

## Package Structure

```
com.aiecommerce.product
├── controller/     ProductController
├── dto/
│   ├── request/    CreateProductRequest
│   └── response/   ReturnProductResponse
├── entity/         BaseEntity, Product, Category
├── exception/      ApplicationException, ResourceNotFoundException
├── mapper/         ProductMapper (MapStruct)
├── repository/     ProductRepository, CategoryRepository
└── service/        ProductService → ProductServiceImpl
```

## Entities

### BaseEntity (abstract)
| Field            | Type      | Description         |
|------------------|-----------|---------------------|
| `isDeleted`      | Boolean   | Soft-delete flag    |
| `createdAt`      | Instant   | Creation timestamp  |
| `createdBy`      | String    | Creator identity    |
| `lastModifiedAt` | Instant   | Last modified time  |
| `lastModifiedBy` | String    | Last modifier       |

> **Note:** `BaseEntity` hiện chưa có `@MappedSuperclass` nên các field audit
> sẽ **không** được Hibernate tự động map xuống DB. Cần thêm annotation để
> các field này hoạt động đúng.

### Product (`products`)
| Field        | Type          | Constraints                     |
|--------------|---------------|---------------------------------|
| `id`         | String (UUID) | PK, auto-generated             |
| `name`       | String        | NOT NULL                        |
| `sku`        | String        | NOT NULL, UNIQUE                |
| `price`      | BigDecimal    | NOT NULL, DECIMAL(15,2)         |
| `stock`      | Integer       | NOT NULL                        |
| `categoryId` | String        | NOT NULL, FK → category.id      |

### Category (`category`)
| Field      | Type          | Constraints                        |
|------------|---------------|------------------------------------|
| `id`       | String (UUID) | PK, auto-generated                |
| `name`     | String        | NOT NULL                           |
| `parent`   | Category      | FK → category.id (self-ref, NULL) |
| `child`    | List<Category>| @OneToMany(mappedBy="parent")     |

## API Endpoints

| Method | Path              | Status   | Description       |
|--------|-------------------|----------|-------------------|
| POST   | `/v1/products`    | Active   | Create a product  |
| GET    | `/v1/products`    | Commented| Get all products  |
| GET    | `/v1/products/{id}`| Commented| Get product by ID |
| PUT    | `/v1/products/{id}`| Commented| Update product    |
| DELETE | `/v1/products/{id}`| Commented| Delete product    |

## Business Rules

- Khi tạo Product, `categoryId` phải tồn tại trong bảng `category`.
  Nếu không → throw `ApplicationException("Category not found")`.
- `sku` là unique, không được trùng.
- `price` phải > 0.

## Known Issues

1. **`BaseEntity` thiếu `@MappedSuperclass`** → field audit không persist xuống DB.
2. **`BaseResponse.erorCode`** → typo, nên là `errorCode`.
3. **HTTP Status mismatch** → `@ResponseStatus(CREATED)` bị override bởi `ResponseEntity.ok()` (trả 200 thay vì 201).
4. **No JPA relationship** giữa Product và Category → `categoryId` là raw String, không có FK constraint ở JPA level (không lazy/eager loading, không cascade).
