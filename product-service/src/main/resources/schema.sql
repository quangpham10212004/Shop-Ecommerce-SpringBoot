-- ============================================================
-- Product Service - Database Init Script
-- Database: product_service_db (MariaDB)
-- ============================================================

CREATE DATABASE IF NOT EXISTS product_service_db;
USE product_service_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- Table: categories
-- Self-referential tree structure (parent_id -> categories.id)
-- ------------------------------------------------------------
CREATE TABLE categories (
    id              CHAR(36)     NOT NULL,
    name            VARCHAR(255) NOT NULL,
    parent_id       CHAR(36)     NULL,
    is_deleted      BIT(1)       NULL DEFAULT 0,
    created_at      DATETIME(6)  NULL,
    created_by      VARCHAR(255) NULL,
    last_modified_at DATETIME(6) NULL,
    last_modified_by VARCHAR(255) NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id) REFERENCES categories (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Table: products
-- FK category_id -> categories.id
-- ------------------------------------------------------------
CREATE TABLE products (
    id              CHAR(36)      NOT NULL,
    name            VARCHAR(255)  NOT NULL,
    sku             VARCHAR(255)  NOT NULL,
    price           DECIMAL(15,2) NOT NULL,
    stock           INT           NOT NULL,
    category_id     CHAR(36)      NOT NULL,
    is_deleted      BIT(1)        NULL DEFAULT 0,
    created_at      DATETIME(6)   NULL,
    created_by      VARCHAR(255)  NULL,
    last_modified_at DATETIME(6)  NULL,
    last_modified_by VARCHAR(255)  NULL,

    PRIMARY KEY (id),

    CONSTRAINT uk_products_sku UNIQUE (sku),

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Sample Data
-- ------------------------------------------------------------

INSERT INTO categories (id, name, parent_id, is_deleted, created_at, created_by)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567801', 'Electronics', NULL, 0, NOW(6), 'system'),
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567802', 'Smartphones', 'a1b2c3d4-e5f6-7890-abcd-ef1234567801', 0, NOW(6), 'system');

INSERT INTO products (id, name, sku, price, stock, category_id, is_deleted, created_at, created_by)
VALUES
    ('b1c2d3e4-f5a6-7890-bcde-f12345678901', 'iPhone 15 Pro', 'IPHONE-15-PRO', 999.99, 50, 'a1b2c3d4-e5f6-7890-abcd-ef1234567802', 0, NOW(6), 'system'),
    ('b1c2d3e4-f5a6-7890-bcde-f12345678902', 'Galaxy S24 Ultra', 'GALAXY-S24-ULTRA', 849.99, 30, 'a1b2c3d4-e5f6-7890-abcd-ef1234567802', 0, NOW(6), 'system');
