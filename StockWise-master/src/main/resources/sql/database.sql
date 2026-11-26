CREATE DATABASE IF NOT EXISTS smart_inventory_db;

USE smart_inventory_db;

-- =========================================
-- 1. SUPPLIERS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS suppliers (
                                         supplier_id INT AUTO_INCREMENT PRIMARY KEY,
                                         supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    lead_time_days INT NOT NULL DEFAULT 7,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- =========================================
-- 2. PRODUCTS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS products (
                                        product_id INT AUTO_INCREMENT PRIMARY KEY,
                                        sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    category_type VARCHAR(50) NOT NULL,
    supplier_id INT NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    selling_price DECIMAL(10,2) NOT NULL DEFAULT 0,
    current_stock INT NOT NULL DEFAULT 0,
    safety_stock INT NOT NULL DEFAULT 10,
    target_coverage_days INT NOT NULL DEFAULT 30,
    expiry_date DATE NULL,
    warranty_months INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_supplier
    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(supplier_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
    );

-- =========================================
-- 3. SALES TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS sales (
                                     sale_id INT AUTO_INCREMENT PRIMARY KEY,
                                     sale_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    payment_method VARCHAR(30) DEFAULT 'CASH',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- =========================================
-- 4. SALE ITEMS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS sale_items (
                                          sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
                                          sale_id INT NOT NULL,
                                          product_id INT NOT NULL,
                                          quantity INT NOT NULL,
                                          unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_saleitem_sale
    FOREIGN KEY (sale_id)
    REFERENCES sales(sale_id)
    ON DELETE CASCADE,

    CONSTRAINT fk_saleitem_product
    FOREIGN KEY (product_id)
    REFERENCES products(product_id)
    ON DELETE RESTRICT
    );

-- =========================================
-- 5. INVENTORY MOVEMENTS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS inventory_movements (
                                                   movement_id INT AUTO_INCREMENT PRIMARY KEY,
                                                   product_id INT NOT NULL,
                                                   movement_type ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    quantity INT NOT NULL,
    reference_id INT NULL,
    notes VARCHAR(255),
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_movement_product
    FOREIGN KEY (product_id)
    REFERENCES products(product_id)
    ON DELETE CASCADE
    );

-- =========================================
-- INDEXES
-- =========================================

CREATE INDEX idx_product_sku
    ON products(sku);

CREATE INDEX idx_product_category
    ON products(category_type);

CREATE INDEX idx_sales_date
    ON sales(sale_date);

CREATE INDEX idx_saleitems_product
    ON sale_items(product_id);

CREATE INDEX idx_saleitems_sale
    ON sale_items(sale_id);

-- =========================================
-- SAMPLE SUPPLIERS
-- =========================================

INSERT INTO suppliers
(supplier_name, contact_person, phone, email, address, lead_time_days)
VALUES
    ('Fresh Foods Distributor', 'Rahul Sharma', '9876543210',
     'fresh@example.com', 'Chandigarh', 3),

    ('Tech Wholesale Pvt Ltd', 'Aman Verma', '9876543211',
     'tech@example.com', 'Delhi', 7),

    ('Fashion Supply Co', 'Neha Gupta', '9876543212',
     'fashion@example.com', 'Ludhiana', 5);

-- =========================================
-- SAMPLE PRODUCTS
-- =========================================

INSERT INTO products
(sku, name, category_type, supplier_id, purchase_price,
 selling_price, current_stock, safety_stock,
 target_coverage_days, expiry_date, warranty_months)
VALUES

-- FOOD PRODUCTS

('FOD-101', 'Milk Pack', 'FOOD', 1,
 30.00, 40.00, 50, 15, 14, '2026-12-15', NULL),

('FOD-102', 'Bread', 'FOOD', 1,
 25.00, 35.00, 20, 10, 7, '2026-11-25', NULL),

('FOD-103', 'Rice Bag', 'FOOD', 1,
 400.00, 500.00, 100, 20, 30, NULL, NULL),

-- ELECTRONICS

('ELE-101', 'Wireless Mouse', 'ELECTRONICS', 2,
 350.00, 599.00, 25, 5, 30, NULL, 12),

('ELE-102', 'Keyboard', 'ELECTRONICS', 2,
 500.00, 799.00, 15, 5, 30, NULL, 12),

('ELE-103', 'Headphones', 'ELECTRONICS', 2,
 800.00, 1299.00, 10, 3, 30, NULL, 24),

-- CLOTHING

('CLO-101', 'Cotton T-Shirt', 'CLOTHING', 3,
 250.00, 499.00, 40, 10, 30, NULL, NULL),

('CLO-102', 'Jeans', 'CLOTHING', 3,
 700.00, 1299.00, 20, 5, 30, NULL, NULL),

('CLO-103', 'Hoodie', 'CLOTHING', 3,
 900.00, 1599.00, 12, 4, 30, NULL, NULL);

-- =========================================
-- SAMPLE SALES
-- =========================================

INSERT INTO sales (sale_date, total_amount, payment_method)
VALUES
    (DATE_SUB(NOW(), INTERVAL 1 DAY), 1200.00, 'CASH'),
    (DATE_SUB(NOW(), INTERVAL 2 DAY), 800.00, 'UPI'),
    (DATE_SUB(NOW(), INTERVAL 5 DAY), 1500.00, 'CARD'),
    (DATE_SUB(NOW(), INTERVAL 10 DAY), 950.00, 'CASH'),
    (DATE_SUB(NOW(), INTERVAL 15 DAY), 2000.00, 'UPI');

-- =========================================
-- SAMPLE SALE ITEMS
-- =========================================

INSERT INTO sale_items
(sale_id, product_id, quantity, unit_price, subtotal)
VALUES
    (1, 1, 10, 40.00, 400.00),
    (1, 4, 2, 599.00, 1198.00),

    (2, 2, 10, 35.00, 350.00),
    (2, 5, 1, 799.00, 799.00),

    (3, 3, 2, 500.00, 1000.00),
    (3, 6, 1, 1299.00, 1299.00),

    (4, 7, 2, 499.00, 998.00),

    (5, 8, 1, 1299.00, 1299.00),
    (5, 9, 1, 1599.00, 1599.00);

-- =========================================
-- INITIAL INVENTORY MOVEMENTS
-- =========================================

INSERT INTO inventory_movements
(product_id, movement_type, quantity, notes)
SELECT
    product_id,
    'IN',
    current_stock,
    'Initial inventory'
FROM products;

-- =========================================
-- VERIFY DATABASE
-- =========================================

SELECT * FROM suppliers;

SELECT * FROM products;

SELECT * FROM sales;

SELECT * FROM sale_items;