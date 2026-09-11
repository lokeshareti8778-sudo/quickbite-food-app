package com.quickbite.foodordering.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long foodId, String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
}