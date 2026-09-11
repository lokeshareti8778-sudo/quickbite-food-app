package com.quickbite.foodordering.dto;

import com.quickbite.foodordering.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        String orderId,
        String customerName,
        String email,
        String phone,
        String address,
        List<OrderItemResponse> items,
        BigDecimal total,
        OrderStatus status,
        String message
) {
}