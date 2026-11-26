package com.inventory.predictor.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inventory.predictor.model.SaleItem;
import com.inventory.predictor.service.SalesService;
import com.inventory.predictor.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SalesController {

    private final SalesService salesService;

    public SalesController() {
        this.salesService = new SalesService();
    }

    public void register(HttpServer server) {

        server.createContext(
                "/api/sales",
                this::handleSales
        );
    }

    private void handleSales(
            HttpExchange exchange
    ) throws IOException {

        try {

            String method =
                    exchange.getRequestMethod();

            // =================================
            // GET SALES
            // =================================

            if ("GET".equalsIgnoreCase(method)) {

                String query =
                        exchange.getRequestURI().getQuery();

                Map<String, String> params =
                        com.inventory.predictor.util.ValidationUtil
                                .parseQuery(query);

                String id = params.get("id");

                if (id != null) {

                    var sale =
                            salesService.getSaleById(
                                    Integer.parseInt(id)
                            );

                    if (sale == null) {

                        JsonUtil.sendError(
                                exchange,
                                404,
                                "Sale not found."
                        );

                        return;
                    }

                    JsonUtil.sendJson(
                            exchange,
                            200,
                            sale
                    );

                } else {

                    JsonUtil.sendJson(
                            exchange,
                            200,
                            salesService.getAllSales()
                    );
                }

                return;
            }

            // =================================
            // CREATE SALE
            // =================================

            if ("POST".equalsIgnoreCase(method)) {

                String body =
                        JsonUtil.readRequestBody(exchange);

                JsonObject json =
                        JsonParser.parseString(body)
                                .getAsJsonObject();

                String paymentMethod =
                        json.has("paymentMethod")
                                ? json.get("paymentMethod")
                                .getAsString()
                                : "CASH";

                JsonArray itemsArray =
                        json.getAsJsonArray("items");

                if (itemsArray == null
                        || itemsArray.isEmpty()) {

                    JsonUtil.sendError(
                            exchange,
                            400,
                            "Sale must contain items."
                    );

                    return;
                }

                List<SaleItem> items =
                        new ArrayList<>();

                for (int i = 0;
                     i < itemsArray.size();
                     i++) {

                    JsonObject item =
                            itemsArray.get(i)
                                    .getAsJsonObject();

                    int productId =
                            item.get("productId")
                                    .getAsInt();

                    int quantity =
                            item.get("quantity")
                                    .getAsInt();

                    if (quantity <= 0) {

                        JsonUtil.sendError(
                                exchange,
                                400,
                                "Quantity must be greater than zero."
                        );

                        return;
                    }

                    items.add(
                            new SaleItem(
                                    productId,
                                    "",
                                    "",
                                    quantity,
                                    0
                            )
                    );
                }

                boolean created =
                        salesService.createSale(
                                items,
                                paymentMethod
                        );

                JsonUtil.sendJson(
                        exchange,
                        201,
                        Map.of(
                                "success", created,
                                "message",
                                "Sale recorded successfully."
                        )
                );

                return;
            }

            JsonUtil.sendError(
                    exchange,
                    405,
                    "Method not allowed."
            );

        } catch (IllegalArgumentException e) {

            JsonUtil.sendError(
                    exchange,
                    400,
                    e.getMessage()
            );

        } catch (SQLException e) {

            JsonUtil.sendError(
                    exchange,
                    500,
                    "Sale error: " + e.getMessage()
            );

        } catch (Exception e) {

            JsonUtil.sendError(
                    exchange,
                    500,
                    "Server error: " + e.getMessage()
            );
        }
    }
}