package com.inventory.predictor.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inventory.predictor.model.*;
import com.inventory.predictor.service.ProductService;
import com.inventory.predictor.util.JsonUtil;
import com.inventory.predictor.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ProductController {

    private final ProductService productService;

    public ProductController() {
        this.productService = new ProductService();
    }

    public void register(HttpServer server) {

        server.createContext(
                "/api/products",
                this::handleProducts
        );
    }

    private void handleProducts(
            HttpExchange exchange
    ) throws IOException {

        try {

            String method =
                    exchange.getRequestMethod();

            String path =
                    exchange.getRequestURI().getPath();

            Map<String, String> params =
                    ValidationUtil.parseQuery(
                            exchange.getRequestURI().getQuery()
                    );

            if ("GET".equalsIgnoreCase(method)) {

                if (path.endsWith("/low-stock")) {

                    JsonUtil.sendJson(
                            exchange,
                            200,
                            productService.getLowStockProducts()
                    );

                    return;
                }

                String id = params.get("id");

                if (id != null) {

                    Product product =
                            productService.getProductById(
                                    Integer.parseInt(id)
                            );

                    if (product == null) {

                        JsonUtil.sendError(
                                exchange,
                                404,
                                "Product not found."
                        );

                        return;
                    }

                    JsonUtil.sendJson(
                            exchange,
                            200,
                            product
                    );

                } else {

                    JsonUtil.sendJson(
                            exchange,
                            200,
                            productService.getAllProducts()
                    );
                }

                return;
            }

            if ("POST".equalsIgnoreCase(method)) {

                String body =
                        JsonUtil.readRequestBody(exchange);

                Product product =
                        parseProduct(body);

                boolean created =
                        productService.createProduct(product);

                JsonUtil.sendJson(
                        exchange,
                        201,
                        Map.of(
                                "success", created,
                                "message", "Product created successfully."
                        )
                );

                return;
            }

            if ("PUT".equalsIgnoreCase(method)) {

                String id = params.get("id");

                if (id == null) {

                    JsonUtil.sendError(
                            exchange,
                            400,
                            "Product ID is required."
                    );

                    return;
                }

                JsonObject json =
                        JsonParser.parseString(
                                JsonUtil.readRequestBody(exchange)
                        ).getAsJsonObject();

                json.addProperty(
                        "productId",
                        Integer.parseInt(id)
                );

                Product product =
                        parseProduct(json.toString());

                boolean updated =
                        productService.updateProduct(product);

                JsonUtil.sendJson(
                        exchange,
                        200,
                        Map.of(
                                "success", updated,
                                "message", "Product updated successfully."
                        )
                );

                return;
            }

            if ("DELETE".equalsIgnoreCase(method)) {

                String id = params.get("id");

                if (id == null) {

                    JsonUtil.sendError(
                            exchange,
                            400,
                            "Product ID is required."
                    );

                    return;
                }

                boolean deleted =
                        productService.deleteProduct(
                                Integer.parseInt(id)
                        );

                JsonUtil.sendJson(
                        exchange,
                        200,
                        Map.of(
                                "success", deleted,
                                "message", "Product deleted successfully."
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
                    "Database error: " + e.getMessage()
            );

        } catch (Exception e) {

            JsonUtil.sendError(
                    exchange,
                    500,
                    "Server error: " + e.getMessage()
            );
        }
    }

    // =====================================
    // CREATE PRODUCT OBJECT FROM JSON
    // =====================================

    private Product parseProduct(String body) {

        JsonObject json =
                JsonParser.parseString(body)
                        .getAsJsonObject();

        int id = json.has("productId")
                ? json.get("productId").getAsInt()
                : 0;

        String sku =
                json.get("sku").getAsString();

        String name =
                json.get("name").getAsString();

        String category =
                json.get("categoryType").getAsString();

        int supplierId =
                json.get("supplierId").getAsInt();

        int leadTime =
                json.has("leadTimeDays")
                        ? json.get("leadTimeDays").getAsInt()
                        : 7;

        double purchasePrice =
                json.get("purchasePrice").getAsDouble();

        double sellingPrice =
                json.get("sellingPrice").getAsDouble();

        int stock =
                json.get("currentStock").getAsInt();

        int safetyStock =
                json.get("safetyStock").getAsInt();

        int coverage =
                json.get("targetCoverageDays").getAsInt();

        if ("FOOD".equalsIgnoreCase(category)) {

            java.time.LocalDate expiry = null;

            if (json.has("expiryDate")
                    && !json.get("expiryDate").isJsonNull()
                    && !json.get("expiryDate")
                    .getAsString().isEmpty()) {

                expiry = java.time.LocalDate.parse(
                        json.get("expiryDate").getAsString()
                );
            }

            return new FoodProduct(
                    id,
                    sku,
                    name,
                    supplierId,
                    leadTime,
                    purchasePrice,
                    sellingPrice,
                    stock,
                    safetyStock,
                    coverage,
                    expiry
            );
        }

        if ("ELECTRONICS".equalsIgnoreCase(category)) {

            int warranty =
                    json.has("warrantyMonths")
                            ? json.get("warrantyMonths").getAsInt()
                            : 12;

            return new ElectronicsProduct(
                    id,
                    sku,
                    name,
                    supplierId,
                    leadTime,
                    purchasePrice,
                    sellingPrice,
                    stock,
                    safetyStock,
                    coverage,
                    warranty
            );
        }

        return new ClothingProduct(
                id,
                sku,
                name,
                supplierId,
                leadTime,
                purchasePrice,
                sellingPrice,
                stock,
                safetyStock,
                coverage
        );
    }
}