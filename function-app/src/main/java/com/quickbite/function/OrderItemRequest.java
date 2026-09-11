package com.quickbite.function;

import java.math.BigDecimal;

public record OrderItemRequest(
        Long foodId,
        String name,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal) {
}