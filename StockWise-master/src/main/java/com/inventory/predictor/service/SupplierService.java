package com.inventory.predictor.service;

import com.inventory.predictor.dao.SupplierDAO;
import com.inventory.predictor.model.Supplier;

import java.sql.SQLException;
import java.util.List;

public class SupplierService {

    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAO();
    }

    // =====================================
    // GET ALL SUPPLIERS
    // =====================================

    public List<Supplier> getAllSuppliers()
            throws SQLException {

        return supplierDAO.getAllSuppliers();
    }

    // =====================================
    // GET SUPPLIER BY ID
    // =====================================

    public Supplier getSupplierById(int id)
            throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid supplier ID."
            );
        }

        return supplierDAO.getSupplierById(id);
    }

    // =====================================
    // ADD SUPPLIER
    // =====================================

    public boolean createSupplier(Supplier supplier)
            throws SQLException {

        validateSupplier(supplier);

        return supplierDAO.addSupplier(supplier);
    }

    // =====================================
    // UPDATE SUPPLIER
    // =====================================

    public boolean updateSupplier(Supplier supplier)
            throws SQLException {

        validateSupplier(supplier);

        if (supplier.getSupplierId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid supplier ID."
            );
        }

        return supplierDAO.updateSupplier(supplier);
    }

    // =====================================
    // DELETE SUPPLIER
    // =====================================

    public boolean deleteSupplier(int supplierId)
            throws SQLException {

        if (supplierId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid supplier ID."
            );
        }

        return supplierDAO.deleteSupplier(supplierId);
    }

    // =====================================
    // SUPPLIER PRODUCT COUNT
    // =====================================

    public int getProductCount(int supplierId)
            throws SQLException {

        return supplierDAO.getProductCount(supplierId);
    }

    // =====================================
    // VALIDATION
    // =====================================

    private void validateSupplier(Supplier supplier) {

        if (supplier == null) {
            throw new IllegalArgumentException(
                    "Supplier cannot be null."
            );
        }

        if (supplier.getSupplierName() == null
                || supplier.getSupplierName()
                .trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Supplier name is required."
            );
        }

        if (supplier.getLeadTimeDays() < 0) {
            throw new IllegalArgumentException(
                    "Lead time cannot be negative."
            );
        }
    }
}