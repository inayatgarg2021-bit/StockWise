package com.inventory.predictor.dao;

import com.inventory.predictor.config.DatabaseConnection;
import com.inventory.predictor.model.Sale;
import com.inventory.predictor.model.SaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesDAO {

    // =====================================
    // GET ALL SALES
    // =====================================

    public List<Sale> getAllSales() throws SQLException {

        List<Sale> sales = new ArrayList<>();

        String sql = """
                SELECT *
                FROM sales
                ORDER BY sale_date DESC
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                int saleId = rs.getInt("sale_id");

                sales.add(new Sale(
                        saleId,
                        rs.getTimestamp("sale_date").toLocalDateTime(),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method"),
                        getSaleItems(saleId)
                ));
            }
        }

        return sales;
    }

    // =====================================
    // GET SALE BY ID
    // =====================================

    public Sale getSaleById(int saleId)
            throws SQLException {

        String sql = """
                SELECT *
                FROM sales
                WHERE sale_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new Sale(
                            rs.getInt("sale_id"),
                            rs.getTimestamp("sale_date").toLocalDateTime(),
                            rs.getDouble("total_amount"),
                            rs.getString("payment_method"),
                            getSaleItems(saleId)
                    );
                }
            }
        }

        return null;
    }

    // =====================================
    // GET SALE ITEMS
    // =====================================

    public List<SaleItem> getSaleItems(int saleId)
            throws SQLException {

        List<SaleItem> items = new ArrayList<>();

        String sql = """
                SELECT si.*,
                       p.name AS product_name,
                       p.sku
                FROM sale_items si
                JOIN products p
                ON si.product_id = p.product_id
                WHERE si.sale_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    items.add(new SaleItem(
                            rs.getInt("sale_item_id"),
                            rs.getInt("sale_id"),
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("sku"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("subtotal")
                    ));
                }
            }
        }

        return items;
    }

    // =====================================
    // RECORD SALE
    // =====================================

    public boolean createSale(
            List<SaleItem> items,
            String paymentMethod
    ) throws SQLException {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Sale must contain at least one product."
            );
        }

        Connection conn = null;

        try {

            conn = DatabaseConnection.getConnection();

            conn.setAutoCommit(false);

            double totalAmount = 0;

            // Validate stock and calculate total

            for (SaleItem item : items) {

                if (item.getQuantity() <= 0) {
                    throw new IllegalArgumentException(
                            "Quantity must be greater than zero."
                    );
                }

                String stockSQL = """
                        SELECT current_stock, selling_price
                        FROM products
                        WHERE product_id = ?
                        FOR UPDATE
                        """;

                try (
                        PreparedStatement ps =
                                conn.prepareStatement(stockSQL)
                ) {

                    ps.setInt(1, item.getProductId());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Product not found: "
                                            + item.getProductId()
                            );
                        }

                        int stock = rs.getInt("current_stock");

                        double price =
                                rs.getDouble("selling_price");

                        if (stock < item.getQuantity()) {

                            throw new SQLException(
                                    "Insufficient stock for product "
                                            + item.getProductName()
                            );
                        }

                        totalAmount +=
                                price * item.getQuantity();
                    }
                }
            }

            // Insert sale record

            String saleSQL = """
                    INSERT INTO sales
                    (total_amount, payment_method)
                    VALUES (?, ?)
                    """;

            int saleId;

            try (
                    PreparedStatement ps =
                            conn.prepareStatement(
                                    saleSQL,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                ps.setDouble(1, totalAmount);
                ps.setString(2, paymentMethod);

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {

                    if (!keys.next()) {
                        throw new SQLException(
                                "Could not create sale."
                        );
                    }

                    saleId = keys.getInt(1);
                }
            }

            // Insert sale items and update stock

            String itemSQL = """
                    INSERT INTO sale_items
                    (sale_id, product_id, quantity, unit_price, subtotal)
                    VALUES (?, ?, ?, ?, ?)
                    """;

            String stockUpdateSQL = """
                    UPDATE products
                    SET current_stock = current_stock - ?
                    WHERE product_id = ?
                    """;

            String movementSQL = """
                    INSERT INTO inventory_movements
                    (product_id, movement_type, quantity,
                     reference_id, notes)
                    VALUES (?, 'OUT', ?, ?, ?)
                    """;

            try (
                    PreparedStatement itemPS =
                            conn.prepareStatement(itemSQL);

                    PreparedStatement stockPS =
                            conn.prepareStatement(stockUpdateSQL);

                    PreparedStatement movementPS =
                            conn.prepareStatement(movementSQL)
            ) {

                for (SaleItem item : items) {

                    double unitPrice;

                    // Fetch the actual product price

                    try (
                            PreparedStatement pricePS =
                                    conn.prepareStatement(
                                            "SELECT selling_price "
                                                    + "FROM products "
                                                    + "WHERE product_id = ?"
                                    )
                    ) {

                        pricePS.setInt(
                                1,
                                item.getProductId()
                        );

                        try (ResultSet rs =
                                     pricePS.executeQuery()) {

                            rs.next();

                            unitPrice =
                                    rs.getDouble("selling_price");
                        }
                    }

                    double subtotal =
                            unitPrice * item.getQuantity();

                    // Insert sale item

                    itemPS.setInt(1, saleId);
                    itemPS.setInt(2, item.getProductId());
                    itemPS.setInt(3, item.getQuantity());
                    itemPS.setDouble(4, unitPrice);
                    itemPS.setDouble(5, subtotal);

                    itemPS.executeUpdate();

                    // Reduce inventory

                    stockPS.setInt(
                            1,
                            item.getQuantity()
                    );

                    stockPS.setInt(
                            2,
                            item.getProductId()
                    );

                    stockPS.executeUpdate();

                    // Record inventory movement

                    movementPS.setInt(
                            1,
                            item.getProductId()
                    );

                    movementPS.setInt(
                            2,
                            item.getQuantity()
                    );

                    movementPS.setInt(
                            3,
                            saleId
                    );

                    movementPS.setString(
                            4,
                            "Sale #" + saleId
                    );

                    movementPS.executeUpdate();
                }
            }

            conn.commit();

            return true;

        } catch (Exception e) {

            if (conn != null) {
                conn.rollback();
            }

            if (e instanceof SQLException sqlException) {
                throw sqlException;
            }

            throw new SQLException(
                    "Failed to create sale.",
                    e
            );

        } finally {

            if (conn != null) {

                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // =====================================
    // TOTAL SALES TODAY
    // =====================================

    public double getSalesToday() throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(total_amount), 0)
                FROM sales
                WHERE DATE(sale_date) = CURDATE()
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }

        return 0;
    }

    // =====================================
    // TOTAL SALES THIS MONTH
    // =====================================

    public double getSalesThisMonth()
            throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(total_amount), 0)
                FROM sales
                WHERE YEAR(sale_date) = YEAR(CURDATE())
                AND MONTH(sale_date) = MONTH(CURDATE())
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }

        return 0;
    }
}