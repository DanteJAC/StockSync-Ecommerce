-- V11__add_stock_requests_and_new_columns.sql

-- 1. Add assigned_warehouse_id to users
ALTER TABLE users ADD COLUMN assigned_warehouse_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_user_assigned_warehouse FOREIGN KEY (assigned_warehouse_id) REFERENCES warehouses(id);

-- 2. Add min_stock_level to products
ALTER TABLE products ADD COLUMN min_stock_level INTEGER DEFAULT 5 NOT NULL;

-- 3. Create stock_requests table
CREATE TABLE stock_requests (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    source_warehouse_id BIGINT NOT NULL,
    destination_warehouse_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_stock_request_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stock_request_source_wh FOREIGN KEY (source_warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_stock_request_dest_wh FOREIGN KEY (destination_warehouse_id) REFERENCES warehouses(id)
);
