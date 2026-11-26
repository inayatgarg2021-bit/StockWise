package com.inventory.predictor.model;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {

    private int saleId;
    private LocalDateTime saleDate;
    private double totalAmount;
    private String paymentMethod;
    private List<SaleItem> items;

    public Sale(
            int saleId,
            LocalDateTime saleDate,
            double totalAmount,
            String paymentMethod,
            List<SaleItem> items) {

        this.saleId = saleId;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }

    public int getSaleId() {
        return saleId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}