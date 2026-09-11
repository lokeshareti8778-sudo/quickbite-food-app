package com.quickbite.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderProcessor {
    private static final AtomicInteger NEXT_ORDER_NUMBER = new AtomicInteger(10001);
    private static final Logger LOGGER = Logger.getLogger(OrderProcessor.class.getName());
    private static final String BUILD_VERSION = Optional.ofNullable(System.getenv("FUNCTION_BUILD_VERSION"))
            .filter(version -> !version.isBlank())
            .orElse("unknown");
    private final ObjectMapper objectMapper;

    public OrderProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> process(String body) throws Exception {
        OrderRequest order;
        try {
            order = objectMapper.readValue(body, OrderRequest.class);
        } catch (JsonProcessingException exception) {
            LOGGER.log(Level.SEVERE,
                    "Order JSON deserialization failed. buildVersion=" + BUILD_VERSION
                            + ", exceptionClass=" + exception.getClass().getName()
                            + ", message=" + exception.getMessage(),
                    exception);
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