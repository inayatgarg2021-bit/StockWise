package com.inventory.predictor.model;

public class DashboardStats {

    private int totalProducts;
    private int totalSuppliers;

    private int lowStockProducts;
    private int criticalStockProducts;
    private int outOfStockProducts;

    private int totalUnitsInStock;

    private double totalInventoryValue;
    private double totalSalesToday;
    private double totalSalesThisMonth;

    public DashboardStats(
            int totalProducts,
            int totalSuppliers,
            int lowStockProducts,
            int criticalStockProducts,
            int outOfStockProducts,
            int totalUnitsInStock,
            double totalInventoryValue,
            double totalSalesToday,
            double totalSalesThisMonth) {

        this.totalProducts = totalProducts;
        this.totalSuppliers = totalSuppliers;

        this.lowStockProducts = lowStockProducts;
        this.criticalStockProducts = criticalStockProducts;
        this.outOfStockProducts = outOfStockProducts;

        this.totalUnitsInStock = totalUnitsInStock;

        this.totalInventoryValue = totalInventoryValue;
        this.totalSalesToday = totalSalesToday;
        this.totalSalesThisMonth = totalSalesThisMonth;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public int getTotalSuppliers() {
        return totalSuppliers;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public int getCriticalStockProducts() {
        return criticalStockProducts;
    }

    public int getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public int getTotalUnitsInStock() {
        return totalUnitsInStock;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public double getTotalSalesToday() {
        return totalSalesToday;
    }

    public double getTotalSalesThisMonth() {
        return totalSalesThisMonth;
    }
}