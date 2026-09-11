package com.quickbite.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderProcessorTest {
    private final OrderProcessor processor = new OrderProcessor(new ObjectMapper());

    @Test
    void confirmsAValidOrder() throws Exception {
        var response = processor.process("{\"customerName\":\"Alex\",\"items\":[{\"foodId\":1}]} ");
        assertEquals("ORD-10001", response.get("orderId"));
        assertEquals("CONFIRMED", response.get("status"));
        assertEquals("Order placed successfully", response.get("message"));
    }

    @Test
    void rejectsAnOrderWithoutItems() {
        assertThrows(IllegalArgumentException.class, () -> processor.process("{\"customerName\":\"Alex\",\"items\":[]}"));
    }
}