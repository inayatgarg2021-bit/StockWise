package com.inventory.predictor.model;

public class ClothingProduct extends Product {

    public ClothingProduct(
            int id,
            String sku,
            String name,
            int supplierId,
            int leadTime,
            double purchasePrice,
            double sellingPrice,
            int stock,
            int safetyStock,
            int coverageDays) {

        super(
                id,
                sku,
                name,
                "CLOTHING",
                supplierId,
                leadTime,
                purchasePrice,
                sellingPrice,
                stock,
                safetyStock,
                coverageDays
        );
    }

    @Override
    public String getSpecificAttributeInfo() {
        return "Clothing Item";
    }
}