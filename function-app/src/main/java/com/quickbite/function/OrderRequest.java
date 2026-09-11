package com.quickbite.function;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        BigDecimal totalAmount,
        String address,
        String email,
        String customerName,
        List<OrderItemRequest> items,
        String phone) {
}