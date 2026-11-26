package com.inventory.predictor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.lang.reflect.Type;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()

            // Handle LocalDate
            .registerTypeAdapter(
                    LocalDate.class,
                    new LocalDateAdapter()
            )

            // Handle LocalDateTime
            .registerTypeAdapter(
                    LocalDateTime.class,
                    new LocalDateTimeAdapter()
            )

            .setPrettyPrinting()
            .create();


    // Convert Java objects to JSON
    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }


    // Convert JSON to Java objects
    public static <T> T fromJson(
            String json,
            Class<T> classType
    ) {
        return GSON.fromJson(json, classType);
    }

    public static <T> T fromJson(
            String json,
            Type type
    ) {
        return GSON.fromJson(json, type);
    }


    // Parse JSON object
    public static JsonObject parseJsonObject(String json) {
        JsonElement element = JsonParser.parseString(json);
        return element.getAsJsonObject();
    }


    // Read request body
    public static String readRequestBody(
            HttpExchange exchange
    ) throws IOException {

        InputStream inputStream = exchange.getRequestBody();

        return new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }


    // Send JSON response
    public static void sendJson(
            HttpExchange exchange,
            int statusCode,
            Object data
    ) throws IOException {

        String response = GSON.toJson(data);

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        try (OutputStream outputStream =
                     exchange.getResponseBody()) {

            outputStream.write(responseBytes);
        }
    }


    // Send error response
    public static void sendError(
            HttpExchange exchange,
            int statusCode,
            String message
    ) throws IOException {

        JsonObject errorObject = new JsonObject();

        errorObject.addProperty("success", false);
        errorObject.addProperty("message", message);

        sendJson(exchange, statusCode, errorObject);
    }


    // LocalDate JSON adapter
    private static class LocalDateAdapter
            implements com.google.gson.JsonSerializer<LocalDate>,
            com.google.gson.JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(
                LocalDate date,
                Type type,
                com.google.gson.JsonSerializationContext context
        ) {

            return date == null
                    ? null
                    : new JsonPrimitive(date.toString());
        }

        @Override
        public LocalDate deserialize(
                JsonElement json,
                Type type,
                com.google.gson.JsonDeserializationContext context
        ) {

            return json == null || json.isJsonNull()
                    ? null
                    : LocalDate.parse(json.getAsString());
        }
    }


    // LocalDateTime JSON adapter
    private static class LocalDateTimeAdapter
            implements com.google.gson.JsonSerializer<LocalDateTime>,
            com.google.gson.JsonDeserializer<LocalDateTime> {

        @Override
        public JsonElement serialize(
                LocalDateTime dateTime,
                Type type,
                com.google.gson.JsonSerializationContext context
        ) {

            return dateTime == null
                    ? null
                    : new JsonPrimitive(dateTime.toString());
        }

        @Override
        public LocalDateTime deserialize(
                JsonElement json,
                Type type,
                com.google.gson.JsonDeserializationContext context
        ) {

            return json == null || json.isJsonNull()
                    ? null
                    : LocalDateTime.parse(json.getAsString());
        }
    }
}