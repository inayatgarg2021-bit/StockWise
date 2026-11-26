package com.inventory.predictor.service;

import com.inventory.predictor.dao.SalesDAO;
import com.inventory.predictor.model.Sale;
import com.inventory.predictor.model.SaleItem;

import java.sql.SQLException;
import java.util.List;

public class SalesService {

    private final SalesDAO salesDAO;

    public SalesService() {
        this.salesDAO = new SalesDAO();
    }

    // =====================================
    // GET ALL SALES
    // =====================================

    public List<Sale> getAllSales()
            throws SQLException {

        return salesDAO.getAllSales();
    }

    // =====================================
    // GET SALE BY ID
    // =====================================

    public Sale getSaleById(int saleId)
            throws SQLException {

        if (saleId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid sale ID."
            );
        }

        return salesDAO.getSaleById(saleId);
    }

    // =====================================
    // CREATE SALE
    // =====================================

    public boolean createSale(
            List<SaleItem> items,
            String paymentMethod
    ) throws SQLException {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please add products to the sale."
            );
        }

        if (paymentMethod == null
                || paymentMethod.trim().isEmpty()) {

            paymentMethod = "CASH";
        }

        return salesDAO.createSale(
                items,
                paymentMethod
        );
    }

    // =====================================
    // SALES TODAY
    // =====================================

    public double getSalesToday()
            throws SQLException {

        return salesDAO.getSalesToday();
    }

    // =====================================
    // SALES THIS MONTH
    // =====================================

    public double getSalesThisMonth()
            throws SQLException {

        return salesDAO.getSalesThisMonth();
    }
}