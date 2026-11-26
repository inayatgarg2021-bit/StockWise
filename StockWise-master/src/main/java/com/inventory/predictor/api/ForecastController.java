package com.inventory.predictor.api;

import com.inventory.predictor.dao.ProductDAO;
import com.inventory.predictor.model.DemandForecast;
import com.inventory.predictor.model.Product;
import com.inventory.predictor.service.DemandPredictor;
import com.inventory.predictor.util.JsonUtil;
import com.inventory.predictor.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForecastController {

    private final ProductDAO productDAO;
    private final DemandPredictor predictor;

    public ForecastController() {

        this.productDAO = new ProductDAO();

        this.predictor = new DemandPredictor();
    }

    public void register(HttpServer server) {

        server.createContext(
                "/api/forecast",
                this::handleForecast
        );
    }

    private void handleForecast(
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

            Map<String, String> params =
                    ValidationUtil.parseQuery(
                            exchange.getRequestURI().getQuery()
                    );

            double multiplier =
                    ValidationUtil.parseDouble(
                            params.get("multiplier"),
                            1.0
                    );

            if (multiplier < 0.1
                    || multiplier > 10.0) {

                JsonUtil.sendError(
                        exchange,
                        400,
                        "Multiplier must be between 0.1 and 10."
                );

                return;
            }

            List<Product> products =
                    productDAO.fetchAllProducts();

            List<DemandForecast> forecasts =
                    new ArrayList<>();

            for (Product product : products) {

                int sold =
                        productDAO.getSoldCount(
                                product.getProductId(),
                                30
                        );

                DemandForecast forecast =
                        predictor.predict(
                                product,
                                sold,
                                30,
                                multiplier
                        );

                forecasts.add(forecast);
            }

            // Highest priority first

            forecasts.sort(
                    (a, b) ->
                            Integer.compare(
                                    b.priorityScore,
                                    a.priorityScore
                            )
            );

            JsonUtil.sendJson(
                    exchange,
                    200,
                    forecasts
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
                    "Forecast error: " + e.getMessage()
            );
        }
    }
}