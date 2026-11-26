package com.inventory.predictor.service;

import com.inventory.predictor.dao.ProductDAO;
import com.inventory.predictor.model.*;

import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    // =====================================
    // GET ALL PRODUCTS
    // =====================================

    public List<Product> getAllProducts()
            throws SQLException {

        return productDAO.fetchAllProducts();
    }

    // =====================================
    // GET PRODUCT
    // =====================================

    public Product getProductById(int id)
            throws SQLException {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID."
            );
        }

        return productDAO.getProductById(id);
    }

    public Product getProductBySKU(String sku)
            throws SQLException {

        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "SKU cannot be empty."
            );
        }

        return productDAO.getProductBySKU(sku);
    }

    // =====================================
    // CREATE PRODUCT
    // =====================================

    public boolean createProduct(Product product)
            throws SQLException {

        validateProduct(product);

        if (productDAO.getProductBySKU(
                product.getSku()) != null) {

            throw new IllegalArgumentException(
                    "SKU already exists."
            );
        }

        return productDAO.addProduct(product);
    }

    // =====================================
    // UPDATE PRODUCT
    // =====================================

    public boolean updateProduct(Product product)
            throws SQLException {

        validateProduct(product);

        if (product.getProductId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID."
            );
        }

        return productDAO.updateProduct(product);
    }

    // =====================================
    // DELETE PRODUCT
    // =====================================

    public boolean deleteProduct(int productId)
            throws SQLException {

        if (productId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid product ID."
            );
        }

        return productDAO.deleteProduct(productId);
    }

    // =====================================
    // RESTOCK PRODUCT
    // =====================================

    public boolean restockProduct(
            String sku,
            int quantity
    ) throws SQLException {

        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "SKU is required."
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        return productDAO.restockProduct(
                sku,
                quantity
        );
    }

    // =====================================
    // LOW STOCK PRODUCTS
    // =====================================

    public List<Product> getLowStockProducts()
            throws SQLException {

        return productDAO.getLowStockProducts();
    }

    // =====================================
    // VALIDATION
    // =====================================

    private void validateProduct(Product product) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null."
            );
        }

        if (product.getSku() == null
                || product.getSku().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "SKU is required."
            );
        }

        if (product.getName() == null
                || product.getName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Product name is required."
            );
        }

        if (product.getPurchasePrice() < 0) {
            throw new IllegalArgumentException(
                    "Purchase price cannot be negative."
            );
        }

        if (product.getSellingPrice() < 0) {
            throw new IllegalArgumentException(
                    "Selling price cannot be negative."
            );
        }

        if (product.getCurrentStock() < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative."
            );
        }

        if (product.getSafetyStock() < 0) {
            throw new IllegalArgumentException(
                    "Safety stock cannot be negative."
            );
        }

        if (product.getTargetCoverageDays() <= 0) {
            throw new IllegalArgumentException(
                    "Coverage days must be greater than zero."
            );
        }
    }
}