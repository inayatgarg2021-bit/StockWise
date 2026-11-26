package com.inventory.predictor.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ValidationUtil {

    // =====================================
    // PARSE QUERY PARAMETERS
    // =====================================

    public static Map<String, String> parseQuery(
            String query
    ) {

        Map<String, String> parameters =
                new HashMap<>();

        if (query == null || query.isEmpty()) {
            return parameters;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {

            String[] parts = pair.split("=", 2);

            String key = decode(parts[0]);

            String value = parts.length > 1
                    ? decode(parts[1])
                    : "";

            parameters.put(key, value);
        }

        return parameters;
    }

    // =====================================
    // URL DECODE
    // =====================================

    private static String decode(String value) {

        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8
        );
    }

    // =====================================
    // SAFE INTEGER
    // =====================================

    public static int parseInt(
            String value,
            int defaultValue
    ) {

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // =====================================
    // SAFE DOUBLE
    // =====================================

    public static double parseDouble(
            String value,
            double defaultValue
    ) {

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // =====================================
    // REQUIRED STRING
    // =====================================

    public static String requireString(
            String value,
            String fieldName
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }

        return value.trim();
    }

    // =====================================
    // POSITIVE INTEGER
    // =====================================

    public static int requirePositiveInt(
            int value,
            String fieldName
    ) {

        if (value <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be greater than zero."
            );
        }

        return value;
    }
}