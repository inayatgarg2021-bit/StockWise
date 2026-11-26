package com.inventory.predictor.model;

import java.time.LocalDate;

public class FoodProduct extends Product {

    private LocalDate expiryDate;

    public FoodProduct(
            int id,
            String sku,
            String name,
            int supplierId,
            int leadTime,
            double purchasePrice,
            double sellingPrice,
            int stock,
            int safetyStock,
            int coverageDays,
            LocalDate expiryDate) {

        super(
                id,
                sku,
                name,
                "FOOD",
                supplierId,
                leadTime,
                purchasePrice,
                sellingPrice,
                stock,
                safetyStock,
                coverageDays
        );

        this.expiryDate = expiryDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    @Override
    public String getSpecificAttributeInfo() {

        if (expiryDate == null) {
            return "Perishable";
        }

        return "Expires: " + expiryDate;
    }
}