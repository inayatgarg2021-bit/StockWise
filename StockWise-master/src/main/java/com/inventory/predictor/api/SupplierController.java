package com.inventory.predictor.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inventory.predictor.model.Supplier;
import com.inventory.predictor.service.SupplierService;
import com.inventory.predictor.util.JsonUtil;
import com.inventory.predictor.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController() {
        this.supplierService = new SupplierService();
    }

    public void register(HttpServer server) {

        server.createContext(
                "/api/suppliers",
                this::handleSuppliers
        );
    }

    private void handleSuppliers(
            HttpExchange exchange
    ) throws IOException {

        try {

            String method =
                    exchange.getRequestMethod();

            Map<String, String> params =
                    ValidationUtil.parseQuery(
                            exchange.getRequestURI().getQuery()
                    );

            // =================================
            // GET
            // =================================

            if ("GET".equalsIgnoreCase(method)) {

                String id = params.get("id");

                if (id != null) {

                    Supplier supplier =
                            supplierService.getSupplierById(
                                    Integer.parseInt(id)
                            );

                    if (supplier == null) {

                        JsonUtil.sendError(
                                exchange,
                                404,
                                "Supplier not found."
                        );

                        return;
                    }

                    JsonUtil.sendJson(
                            exchange,
                            200,
                            supplier
                    );

                } else {

                    JsonUtil.sendJson(
                            exchange,
                            200,
                            supplierService.getAllSuppliers()
                    );
                }

                return;
            }

            // =================================
            // POST
            // =================================

            if ("POST".equalsIgnoreCase(method)) {

                Supplier supplier =
                        parseSupplier(
                                JsonUtil.readRequestBody(exchange)
                        );

                boolean created =
                        supplierService.createSupplier(
                                supplier
                        );

                JsonUtil.sendJson(
                        exchange,
                        201,
                        Map.of(
                                "success", created,
                                "message",
                                "Supplier created successfully."
                        )
                );

                return;
            }

            // =================================
            // PUT
            // =================================

            if ("PUT".equalsIgnoreCase(method)) {

                String id = params.get("id");

                if (id == null) {

                    JsonUtil.sendError(
                            exchange,
                            400,
                            "Supplier ID is required."
                    );

                    return;
                }

                JsonObject json =
                        JsonParser.parseString(
                                JsonUtil.readRequestBody(exchange)
                        ).getAsJsonObject();

                json.addProperty(
                        "supplierId",
                        Integer.parseInt(id)
                );

                Supplier supplier =
                        parseSupplier(json.toString());

                boolean updated =
                        supplierService.updateSupplier(
                                supplier
                        );

                JsonUtil.sendJson(
                        exchange,
                        200,
                        Map.of(
                                "success", updated,
                                "message",
                                "Supplier updated successfully."
                        )
                );

                return;
            }

            // =================================
            // DELETE
            // =================================

            if ("DELETE".equalsIgnoreCase(method)) {

                String id = params.get("id");

                if (id == null) {

                    JsonUtil.sendError(
                            exchange,
                            400,
                            "Supplier ID is required."
                    );

                    return;
                }

                boolean deleted =
                        supplierService.deleteSupplier(
                                Integer.parseInt(id)
                        );

                JsonUtil.sendJson(
                        exchange,
                        200,
                        Map.of(
                                "success", deleted,
                                "message",
                                "Supplier deleted successfully."
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
    // JSON TO SUPPLIER
    // =====================================

    private Supplier parseSupplier(String body) {

        JsonObject json =
                JsonParser.parseString(body)
                        .getAsJsonObject();

        int id = json.has("supplierId")
                ? json.get("supplierId").getAsInt()
                : 0;

        String supplierName =
                json.get("supplierName").getAsString();

        String contactPerson =
                json.has("contactPerson")
                        && !json.get("contactPerson").isJsonNull()
                        ? json.get("contactPerson").getAsString()
                        : "";

        String phone =
                json.has("phone")
                        && !json.get("phone").isJsonNull()
                        ? json.get("phone").getAsString()
                        : "";

        String email =
                json.has("email")
                        && !json.get("email").isJsonNull()
                        ? json.get("email").getAsString()
                        : "";

        String address =
                json.has("address")
                        && !json.get("address").isJsonNull()
                        ? json.get("address").getAsString()
                        : "";

        int leadTime =
                json.has("leadTimeDays")
                        ? json.get("leadTimeDays").getAsInt()
                        : 7;

        return new Supplier(
                id,
                supplierName,
                contactPerson,
                phone,
                email,
                address,
                leadTime
        );
    }
}