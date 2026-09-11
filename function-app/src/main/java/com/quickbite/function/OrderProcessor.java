package com.quickbite.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderProcessor {
    private static final AtomicInteger NEXT_ORDER_NUMBER = new AtomicInteger(10001);
    private final ObjectMapper objectMapper;

    public OrderProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> process(String body) throws Exception {
        OrderRequest order;
        try {
            order = objectMapper.readValue(body, OrderRequest.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid order JSON", exception);
        }
        if (order == null || order.customerName() == null || order.customerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (order.items() == null || order.items().isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required");
        }
        return Map.of(
                "orderId", "ORD-" + NEXT_ORDER_NUMBER.getAndIncrement(),
                "status", "CONFIRMED",
                "message", "Order placed successfully"
        );
    }
}