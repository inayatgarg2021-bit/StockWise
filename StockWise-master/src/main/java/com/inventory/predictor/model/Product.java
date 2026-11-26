package com.inventory.predictor.model;

public abstract class Product {

    private int productId;
    private String sku;
    private String name;
    private String categoryType;
    private int supplierId;
    private int leadTimeDays;
    private double purchasePrice;
    private double sellingPrice;
    private int currentStock;
    private int safetyStock;
    private int targetCoverageDays;

    public Product(
            int productId,
            String sku,
            String name,
            String categoryType,
            int supplierId,
            int leadTimeDays,
            double purchasePrice,
            double sellingPrice,
            int currentStock,
            int safetyStock,
            int targetCoverageDays) {

        this.productId = productId;
        this.sku = sku;
        this.name = name;
        this.categoryType = categoryType;
        this.supplierId = supplierId;
        this.leadTimeDays = leadTimeDays;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.currentStock = currentStock;
        this.safetyStock = safetyStock;
        this.targetCoverageDays = targetCoverageDays;
    }

    public abstract String getSpecificAttributeInfo();

    // Getters

    public int getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public int getLeadTimeDays() {
        return leadTimeDays;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public int getSafetyStock() {
        return safetyStock;
    }

    public int getTargetCoverageDays() {
        return targetCoverageDays;
    }

    // Setters

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}