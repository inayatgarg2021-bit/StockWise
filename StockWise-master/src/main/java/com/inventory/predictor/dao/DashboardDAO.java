
package com.inventory.predictor.dao;

import com.inventory.predictor.config.DatabaseConnection;
import com.inventory.predictor.model.DashboardStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    // =====================================
    // GET DASHBOARD STATISTICS
    // =====================================

    public DashboardStats getDashboardStats()
            throws SQLException {

        int totalProducts = getTotalProducts();

        int totalSuppliers = getTotalSuppliers();

        int lowStock = getLowStockProducts();

        int criticalStock = getCriticalStockProducts();

        int outOfStock = getOutOfStockProducts();

        int totalUnits = getTotalUnits();

        double inventoryValue = getInventoryValue();

        double salesToday = getSalesToday();

        double salesThisMonth = getSalesThisMonth();

        return new DashboardStats(
                totalProducts,
                totalSuppliers,
                lowStock,
                criticalStock,
                outOfStock,
                totalUnits,
                inventoryValue,
                salesToday,
                salesThisMonth
        );
    }

    // =====================================
    // TOTAL PRODUCTS
    // =====================================

    public int getTotalProducts() throws SQLException {

        return getIntValue(
                "SELECT COUNT(*) FROM products"
        );
    }

    // =====================================
    // TOTAL SUPPLIERS
    // =====================================

    public int getTotalSuppliers() throws SQLException {

        return getIntValue(
                "SELECT COUNT(*) FROM suppliers"
        );
    }

    // =====================================
    // LOW STOCK PRODUCTS
    // =====================================

    public int getLowStockProducts()
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM products
                WHERE current_stock > safety_stock * 0.5
                AND current_stock <= safety_stock
                """;

        return getIntValue(sql);
    }

    // =====================================
    // CRITICAL STOCK PRODUCTS
    // =====================================

    public int getCriticalStockProducts()
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM products
                WHERE current_stock > 0
                AND current_stock <= safety_stock * 0.5
                """;

        return getIntValue(sql);
    }

    // =====================================
    // OUT OF STOCK PRODUCTS
    // =====================================

    public int getOutOfStockProducts()
            throws SQLException {

        return getIntValue(
                "SELECT COUNT(*) FROM products "
                        + "WHERE current_stock = 0"
        );
    }

    // =====================================
    // TOTAL STOCK UNITS
    // =====================================

    public int getTotalUnits() throws SQLException {

        return getIntValue(
                "SELECT COALESCE(SUM(current_stock), 0) "
                        + "FROM products"
        );
    }

    // =====================================
    // INVENTORY VALUE
    // =====================================

    public double getInventoryValue()
            throws SQLException {

        String sql = """
                SELECT COALESCE(
                    SUM(current_stock * purchase_price),
                    0
                )
                FROM products
                """;

        return getDoubleValue(sql);
    }

    // =====================================
    // SALES TODAY
    // =====================================

    public double getSalesToday()
            throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(total_amount), 0)
                FROM sales
                WHERE DATE(sale_date) = CURDATE()
                """;

        return getDoubleValue(sql);
    }

    // =====================================
    // SALES THIS MONTH
    // =====================================

    public double getSalesThisMonth()
            throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(total_amount), 0)
                FROM sales
                WHERE MONTH(sale_date) = MONTH(CURDATE())
                AND YEAR(sale_date) = YEAR(CURDATE())
                """;

        return getDoubleValue(sql);
    }

    // =====================================
    // GENERIC INTEGER QUERY
    // =====================================

    private int getIntValue(String sql)
            throws SQLException {

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    // =====================================
    // GENERIC DOUBLE QUERY
    // =====================================

    private double getDoubleValue(String sql)
            throws SQLException {

        try (
                Connection conn =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }

        return 0;
    }
}
