package com.inventory.predictor.model;

public class DemandForecast {

    public String sku;
    public String name;
    public String category;
    public String attributeInfo;

    public int currentStock;

    public double avgDailySales;
    public double estimatedDaysRemaining;

    public int reorderPoint;
    public int suggestedReorderQty;

    public String status;
    public int priorityScore;

    public double purchasePrice;
    public double sellingPrice;

    public int supplierId;
    public int leadTimeDays;

    public DemandForecast() {
    }
}