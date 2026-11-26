package com.inventory.predictor.model;

public class ElectronicsProduct extends Product {

    private int warrantyMonths;

    public ElectronicsProduct(
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
            int warrantyMonths) {

        super(
                id,
                sku,
                name,
                "ELECTRONICS",
                supplierId,
                leadTime,
                purchasePrice,
                sellingPrice,
                stock,
                safetyStock,
                coverageDays
        );

        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public String getSpecificAttributeInfo() {
        return warrantyMonths + " Months Warranty";
    }
}