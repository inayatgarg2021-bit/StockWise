package com.inventory.predictor;

import com.inventory.predictor.api.DashboardController;
import com.inventory.predictor.api.ForecastController;
import com.inventory.predictor.api.ProductController;
import com.inventory.predictor.api.SalesController;
import com.inventory.predictor.api.SupplierController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class ServerApp {

    private static final int PORT = 8080;

    public static void main(String[] args)
            throws IOException {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0
                );

        // =====================================
        // REGISTER API CONTROLLERS
        // =====================================

        ProductController productController =
                new ProductController();

        SalesController salesController =
                new SalesController();

        SupplierController supplierController =
                new SupplierController();

        ForecastController forecastController =
                new ForecastController();

        DashboardController dashboardController =
                new DashboardController();

        productController.register(server);

        salesController.register(server);

        supplierController.register(server);

        forecastController.register(server);

        dashboardController.register(server);

        // =====================================
        // STATIC FILE SERVER
        // =====================================

        server.createContext(
                "/",
                ServerApp::handleStaticFiles
        );

        // =====================================
        // SERVER THREAD POOL
        // =====================================

        server.setExecutor(
                Executors.newFixedThreadPool(10)
        );

        // =====================================
        // START SERVER
        // =====================================

        server.start();

        System.out.println("------------------------------------");
        System.out.println("SmartStock AI is running!");
        System.out.println(
                "Open: http://localhost:" + PORT
        );
        System.out.println("------------------------------------");
    }

    // =====================================
    // STATIC FILE HANDLER
    // =====================================

    private static void handleStaticFiles(
            HttpExchange exchange
    ) throws IOException {

        // CORS

        addCorsHeaders(exchange);

        // Handle browser preflight requests

        if ("OPTIONS".equalsIgnoreCase(
                exchange.getRequestMethod()
        )) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            exchange.close();

            return;
        }

        String path =
                exchange.getRequestURI().getPath();

        // Default homepage

        if (path.equals("/")) {
            path = "/index.html";
        }

        // Prevent directory traversal

        if (path.contains("..")) {

            sendText(
                    exchange,
                    403,
                    "Forbidden"
            );

            return;
        }

        // Resource location

        String resourcePath =
                "/web" + path;

        try (
                InputStream input =
                        ServerApp.class
                                .getResourceAsStream(
                                        resourcePath
                                )
        ) {

            if (input == null) {

                sendText(
                        exchange,
                        404,
                        "404 - File Not Found"
                );

                return;
            }

            byte[] content =
                    input.readAllBytes();

            String contentType =
                    getContentType(path);

            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            contentType
                                    + "; charset=UTF-8"
                    );

            exchange.sendResponseHeaders(
                    200,
                    content.length
            );

            try (
                    OutputStream output =
                            exchange.getResponseBody()
            ) {

                output.write(content);
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendText(
                    exchange,
                    500,
                    "Internal Server Error"
            );
        }
    }

    // =====================================
    // CONTENT TYPE
    // =====================================

    private static String getContentType(
            String path
    ) {

        if (path.endsWith(".html")) {
            return "text/html";
        }

        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        if (path.endsWith(".json")) {
            return "application/json";
        }

        if (path.endsWith(".png")) {
            return "image/png";
        }

        if (path.endsWith(".jpg")
                || path.endsWith(".jpeg")) {

            return "image/jpeg";
        }

        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }

        if (path.endsWith(".ico")) {
            return "image/x-icon";
        }

        if (path.endsWith(".woff")) {
            return "font/woff";
        }

        if (path.endsWith(".woff2")) {
            return "font/woff2";
        }

        return "text/plain";
    }

    // =====================================
    // CORS HEADERS
    // =====================================

    private static void addCorsHeaders(
            HttpExchange exchange
    ) {

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "*"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Methods",
                        "GET, POST, PUT, DELETE, OPTIONS"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );
    }

    // =====================================
    // SEND TEXT
    // =====================================

    private static void sendText(
            HttpExchange exchange,
            int statusCode,
            String message
    ) throws IOException {

        byte[] bytes =
                message.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        try (
                OutputStream output =
                        exchange.getResponseBody()
        ) {

            output.write(bytes);
        }
    }
}