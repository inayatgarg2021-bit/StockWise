package com.inventory.predictor.dao;

import com.inventory.predictor.config.DatabaseConnection;
import com.inventory.predictor.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    // =====================================
    // GET ALL SUPPLIERS
    // =====================================

    public List<Supplier> getAllSuppliers() throws SQLException {

        List<Supplier> suppliers = new ArrayList<>();

        String sql = """
                SELECT *
                FROM suppliers
                ORDER BY supplier_id DESC
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                suppliers.add(mapSupplier(rs));
            }
        }

        return suppliers;
    }

    // =====================================
    // GET SUPPLIER BY ID
    // =====================================

    public Supplier getSupplierById(int supplierId)
            throws SQLException {

        String sql = """
                SELECT *
                FROM suppliers
                WHERE supplier_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        }

        return null;
    }

    // =====================================
    // ADD SUPPLIER
    // =====================================

    public boolean addSupplier(Supplier supplier)
            throws SQLException {

        String sql = """
                INSERT INTO suppliers
                (
                    supplier_name,
                    contact_person,
                    phone,
                    email,
                    address,
                    lead_time_days
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setInt(6, supplier.getLeadTimeDays());

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================
    // UPDATE SUPPLIER
    // =====================================

    public boolean updateSupplier(Supplier supplier)
            throws SQLException {

        String sql = """
                UPDATE suppliers
                SET supplier_name = ?,
                    contact_person = ?,
                    phone = ?,
                    email = ?,
                    address = ?,
                    lead_time_days = ?
                WHERE supplier_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, supplier.getSupplierName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setInt(6, supplier.getLeadTimeDays());
            ps.setInt(7, supplier.getSupplierId());

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================
    // DELETE SUPPLIER
    // =====================================

    public boolean deleteSupplier(int supplierId)
            throws SQLException {

        String sql = """
                DELETE FROM suppliers
                WHERE supplier_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, supplierId);

            return ps.executeUpdate() > 0;
        }
    }

    // =====================================
    // GET SUPPLIER PRODUCT COUNT
    // =====================================

    public int getProductCount(int supplierId)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM products
                WHERE supplier_id = ?
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, supplierId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    // =====================================
    // MAP RESULT SET
    // =====================================

    private Supplier mapSupplier(ResultSet rs)
            throws SQLException {

        return new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("contact_person"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getInt("lead_time_days")
        );
    }
}