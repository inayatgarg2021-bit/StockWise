package com.inventory.predictor.api;

import com.inventory.predictor.service.DashboardService;
import com.inventory.predictor.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController() {
        this.dashboardService = new DashboardService();
    }

    public void register(HttpServer server) {

        server.createContext(
                "/api/dashboard",
                this::handleDashboard
        );
    }

    private void handleDashboard(
            HttpExchange exchange
    ) throws IOException {

        try {

            if (!"GET".equalsIgnoreCase(
                    exchange.getRequestMethod()
            )) {

                JsonUtil.sendError(
                        exchange,
                        405,
                        "Method not allowed."
                );

                return;
            }

            JsonUtil.sendJson(
                    exchange,
                    200,
                    dashboardService.getDashboardStats()
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
                    "Dashboard error: " + e.getMessage()
            );
        }
    }
}