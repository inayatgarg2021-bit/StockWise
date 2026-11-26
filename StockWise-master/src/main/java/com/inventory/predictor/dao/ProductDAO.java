package com.inventory.predictor.dao;

import com.inventory.predictor.config.DatabaseConnection;
import com.inventory.predictor.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // =====================================
    // GET ALL PRODUCTS
    // =====================================

    public List<Product> fetchAllProducts() throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT p.*, s.lead_time_days
                FROM products p
                JOIN suppliers s
                ON p.supplier_id = s.supplier_id
                ORDER BY p.product_id DESC
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        }

        return products;
    }

    // =====================================
    // GET PRODUCT BY ID
    // =====================================

    public Product getProductById(int productId) throws SQLException {

        String sql = """
                SELECT p.*, s.lead_time_days
                FROM products p
                JOIN suppliers s
                ON p.supplier_id = s.supplier_id
                WHERE p.product_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }

        return null;
    }

    // =====================================
    // GET PRODUCT BY SKU
    // =====================================

    public Product getProductBySKU(String sku) throws SQLException {

        String sql = """
                SELECT p.*, s.lead_time_days
                FROM products p
                JOIN suppliers s
                ON p.supplier_id = s.supplier_id
                WHERE p.sku = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, sku);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapProduct(rs);
                }
            }
        }

        return null;
    }

    // =====================================
    // ADD PRODUCT
    // =====================================

    public boolean addProduct(Product product) throws SQLException {

        String sql = """
                INSERT INTO products
                (
                    sku,
                    name,
                    category_type,
                    supplier_id,
                    purchase_price,
                    selling_price,
                    current_stock,
                    safety_stock,
                    target_coverage_days,
                    expiry_date,
                    warranty_months
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, product.getSku());
            ps.setString(2, product.getName());
            ps.setString(3, product.getCategoryType());
            ps.setInt(4, product.getSupplierId());
            ps.setDouble(5, product.getPurchasePrice());
            ps.setDouble(6, product.getSellingPrice());
            ps.setInt(7, product.getCurrentStock());
            ps.setInt(8, product.getSafetyStock());
            ps.setInt(9, product.getTargetCoverageDays());

            if (product instanceof FoodProduct food) {

                if (food.getExpiryDate() != null) {
                    ps.setDate(10, Date.valueOf(food.getExpiryDate()));
                } else {
                    ps.setNull(10, Types.DATE);
                }

                ps.setNull(11, Types.INTEGER);

            } else if (product instanceof ElectronicsProduct electronics) {

                ps.setNull(10, Types.DATE);
                ps.setInt(11, electronics.getWarrantyMonths());

            } else {

                ps.setNull(10, Types.DATE);
                ps.setNull(11, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================
    // UPDATE PRODUCT
    // =====================================

    public boolean updateProduct(Product product) throws SQLException {

        String sql = """
                UPDATE products
                SET name = ?,
                    category_type = ?,
                    supplier_id = ?,
                    purchase_price = ?,
                    selling_price = ?,
                    current_stock = ?,
                    safety_stock = ?,
                    target_coverage_days = ?,
                    expiry_date = ?,
                    warranty_months = ?
                WHERE product_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getCategoryType());
            ps.setInt(3, product.getSupplierId());
            ps.setDouble(4, product.getPurchasePrice());
            ps.setDouble(5, product.getSellingPrice());
            ps.setInt(6, product.getCurrentStock());
            ps.setInt(7, product.getSafetyStock());
            ps.setInt(8, product.getTargetCoverageDays());

            if (product instanceof FoodProduct food
                    && food.getExpiryDate() != null) {

                ps.setDate(9, Date.valueOf(food.getExpiryDate()));

            } else {

                ps.setNull(9, Types.DATE);
            }

            if (product instanceof ElectronicsProduct electronics) {

                ps.setInt(10, electronics.getWarrantyMonths());

            } else {

                ps.setNull(10, Types.INTEGER);
            }

            ps.setInt(11, product.getProductId());

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================
    // DELETE PRODUCT
    // =====================================

    public boolean deleteProduct(int productId) throws SQLException {

        String sql = "DELETE FROM products WHERE product_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, productId);

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================
    // RESTOCK PRODUCT
    // =====================================

    public boolean restockProduct(String sku, int quantity)
            throws SQLException {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Restock quantity must be greater than zero."
            );
        }

        String sql = """
                UPDATE products
                SET current_stock = current_stock + ?
                WHERE sku = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, quantity);
            ps.setString(2, sku);

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================
    // GET SOLD QUANTITY
    // =====================================

    public int getSoldCount(int productId, int days)
            throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(si.quantity), 0)
                FROM sale_items si
                JOIN sales s
                ON si.sale_id = s.sale_id
                WHERE si.product_id = ?
                AND s.sale_date >= DATE_SUB(NOW(), INTERVAL ? DAY)
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, productId);
            ps.setInt(2, days);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    // =====================================
    // GET LOW STOCK PRODUCTS
    // =====================================

    public List<Product> getLowStockProducts()
            throws SQLException {

        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT p.*, s.lead_time_days
                FROM products p
                JOIN suppliers s
                ON p.supplier_id = s.supplier_id
                WHERE p.current_stock <= p.safety_stock
                ORDER BY p.current_stock ASC
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        }

        return products;
    }

    // =====================================
    // MAP DATABASE ROW TO PRODUCT OBJECT
    // =====================================

    private Product mapProduct(ResultSet rs)
            throws SQLException {

        int id = rs.getInt("product_id");

        String sku = rs.getString("sku");
        String name = rs.getString("name");
        String category = rs.getString("category_type");

        int supplierId = rs.getInt("supplier_id");
        int leadTime = rs.getInt("lead_time_days");

        double purchasePrice = rs.getDouble("purchase_price");
        double sellingPrice = rs.getDouble("selling_price");

        int stock = rs.getInt("current_stock");
        int safetyStock = rs.getInt("safety_stock");
        int coverage = rs.getInt("target_coverage_days");

        if ("FOOD".equalsIgnoreCase(category)) {

            Date expiryDate = rs.getDate("expiry_date");

            return new FoodProduct(
                    id,
                    sku,
                    name,
                    supplierId,
                    leadTime,
                    purchasePrice,
                    sellingPrice,
                    stock,
                    safetyStock,
                    coverage,
                    expiryDate != null
                            ? expiryDate.toLocalDate()
                            : null
            );
        }

        if ("ELECTRONICS".equalsIgnoreCase(category)) {

            int warranty = rs.getInt("warranty_months");

            return new ElectronicsProduct(
                    id,
                    sku,
                    name,
                    supplierId,
                    leadTime,
                    purchasePrice,
                    sellingPrice,
                    stock,
                    safetyStock,
                    coverage,
                    warranty
            );
        }

        return new ClothingProduct(
                id,
                sku,
                name,
                supplierId,
                leadTime,
                purchasePrice,
                sellingPrice,
                stock,
                safetyStock,
                coverage
        );
    }
}