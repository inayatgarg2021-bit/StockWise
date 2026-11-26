package com.inventory.predictor.service;

import com.inventory.predictor.model.DemandForecast;
import com.inventory.predictor.model.Product;

public class DemandPredictor {

    public DemandForecast predict(
            Product product,
            int totalSold,
            int lookbackDays,
            double whatIfMultiplier
    ) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null."
            );
        }

        if (lookbackDays <= 0) {
            lookbackDays = 30;
        }

        if (whatIfMultiplier <= 0) {
            whatIfMultiplier = 1.0;
        }

        // =====================================
        // AVERAGE DAILY DEMAND
        // =====================================

        double baseDailyDemand =
                (double) totalSold / lookbackDays;

        double averageDailySales =
                baseDailyDemand * whatIfMultiplier;

        // =====================================
        // DAYS REMAINING
        // =====================================

        double daysRemaining;

        if (product.getCurrentStock() <= 0) {

            daysRemaining = 0;

        } else if (averageDailySales <= 0.001) {

            daysRemaining = 999;

        } else {

            daysRemaining =
                    product.getCurrentStock()
                            / averageDailySales;
        }

        // =====================================
        // REORDER POINT
        // =====================================

        int reorderPoint = (int) Math.ceil(
                averageDailySales
                        * product.getLeadTimeDays()
                        + product.getSafetyStock()
        );

        // =====================================
        // TARGET STOCK
        // =====================================

        int targetStock = (int) Math.ceil(
                averageDailySales
                        * product.getTargetCoverageDays()
                        + product.getSafetyStock()
        );

        // =====================================
        // SUGGESTED REORDER QUANTITY
        // =====================================

        int suggestedQuantity = Math.max(
                0,
                targetStock - product.getCurrentStock()
        );

        // =====================================
        // STOCK STATUS
        // =====================================

        String status;
        int priority;

        if (product.getCurrentStock() <= 0) {

            status = "OUT OF STOCK";
            priority = 100;

        } else if (
                daysRemaining
                        <= product.getLeadTimeDays()
        ) {

            status = "CRITICAL";
            priority = 85;

        } else if (
                product.getCurrentStock()
                        <= reorderPoint
        ) {

            status = "LOW STOCK";
            priority = 65;

        } else {

            status = "HEALTHY";

            /*
             * Dynamic priority:
             * Products with fewer days of stock
             * remaining receive higher priority.
             */

            if (daysRemaining >= 999) {

                priority = 20;

            } else {

                double urgencyRatio =
                        product.getLeadTimeDays()
                                / daysRemaining;

                priority = (int) Math.ceil(
                        20 + (urgencyRatio * 40)
                );

                priority = Math.min(
                        60,
                        Math.max(20, priority)
                );
            }
        }

        // =====================================
        // CREATE FORECAST OBJECT
        // =====================================

        DemandForecast forecast =
                new DemandForecast();

        forecast.sku =
                product.getSku();

        forecast.name =
                product.getName();

        forecast.category =
                product.getCategoryType();

        forecast.attributeInfo =
                product.getSpecificAttributeInfo();

        forecast.currentStock =
                product.getCurrentStock();

        forecast.avgDailySales =
                Math.round(
                        averageDailySales * 100.0
                ) / 100.0;

        forecast.estimatedDaysRemaining =
                Math.round(
                        daysRemaining * 10.0
                ) / 10.0;

        forecast.reorderPoint =
                reorderPoint;

        forecast.suggestedReorderQty =
                suggestedQuantity;

        forecast.status =
                status;

        forecast.priorityScore =
                priority;

        forecast.purchasePrice =
                product.getPurchasePrice();

        forecast.sellingPrice =
                product.getSellingPrice();

        forecast.supplierId =
                product.getSupplierId();

        forecast.leadTimeDays =
                product.getLeadTimeDays();

        return forecast;
    }
}