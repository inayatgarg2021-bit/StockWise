package com.inventory.predictor.config;

import com.inventory.predictor.dao.ProductDAO;
import com.inventory.predictor.model.Product;

import java.util.List;

public class DatabaseTest {

    public static void main(String[] args) {

        try {
            // Test database connection
            System.out.println("================================");
            System.out.println("DATABASE CONNECTION SUCCESSFUL!");
            System.out.println("================================");

            // Fetch products from MySQL
            ProductDAO productDAO = new ProductDAO();
            List<Product> products = productDAO.fetchAllProducts();

            System.out.println("\nPRODUCTS FETCHED SUCCESSFULLY!");
            System.out.println("Total Products: " + products.size());
            System.out.println("--------------------------------");

            // Display product details
            for (Product product : products) {
                System.out.println("SKU: " + product.getSku());
                System.out.println("Name: " + product.getName());
                System.out.println("Category: " + product.getCategoryType());
                System.out.println("Stock: " + product.getCurrentStock());
                System.out.println("--------------------------------");
            }

        } catch (Exception e) {
            System.out.println("TEST FAILED!");
            e.printStackTrace();
        }
    }
}