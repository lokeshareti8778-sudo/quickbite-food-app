package com.quickbite.foodordering.model;

import java.math.BigDecimal;

public record FoodItem(
        Long id,
        String name,
        String description,
        BigDecimal price,
        FoodCategory category,
        String imageUrl,
        boolean vegetarian
) {
}