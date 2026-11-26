package com.inventory.predictor.model;

public class SaleItem {

    private int saleItemId;
    private int saleId;
    private int productId;
    private String productName;
    private String sku;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public SaleItem(
            int saleItemId,
            int saleId,
            int productId,
            String productName,
            String sku,
            int quantity,
            double unitPrice,
            double subtotal) {

        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public SaleItem(
            int productId,
            String productName,
            String sku,
            int quantity,
            double unitPrice) {

        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public int getSaleItemId() {
        return saleItemId;
    }

    public int getSaleId() {
        return saleId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }
}