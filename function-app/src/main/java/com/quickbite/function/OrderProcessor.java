package com.quickbite.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderProcessor {
    private static final AtomicInteger NEXT_ORDER_NUMBER = new AtomicInteger(10001);
    private static final String ITEMS_FIELD = "items";
    private final ObjectMapper objectMapper;

    public OrderProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> process(String body) throws Exception {
        JsonNode order = objectMapper.readTree(body);
        if (order == null || !order.hasNonNull("customerName") || order.get("customerName").asText().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (!order.has(ITEMS_FIELD) || !order.get(ITEMS_FIELD).isArray() || order.get(ITEMS_FIELD).isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required");
        }
        return Map.of(
                "orderId", "ORD-" + NEXT_ORDER_NUMBER.getAndIncrement(),
                "status", "CONFIRMED",
                "message", "Order placed successfully"
        );
    }
}