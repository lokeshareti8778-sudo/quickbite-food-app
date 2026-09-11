package com.quickbite.foodordering.repository;

import com.quickbite.foodordering.model.FoodCategory;
import com.quickbite.foodordering.model.FoodItem;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class FoodRepository {
    private final List<FoodItem> menu = List.of(
            new FoodItem(1L, "Smoky BBQ Burger", "Charred beef, smoked cheddar, crispy onions and house BBQ sauce.", new BigDecimal("12.50"), FoodCategory.BURGERS, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=80", false),
            new FoodItem(2L, "Garden Pesto Pizza", "Roasted vegetables, basil pesto, mozzarella and blistered tomatoes.", new BigDecimal("14.00"), FoodCategory.PIZZA, "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=900&q=80", true),
            new FoodItem(3L, "Teriyaki Salmon Bowl", "Miso salmon, jasmine rice, edamame, pickled cucumber and sesame.", new BigDecimal("16.50"), FoodCategory.BOWLS, "https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=900&q=80", false),
            new FoodItem(4L, "Truffle Parmesan Fries", "Golden fries tossed with truffle oil, parmesan and fresh herbs.", new BigDecimal("7.50"), FoodCategory.SIDES, "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=900&q=80", true),
            new FoodItem(5L, "Chocolate Lava Cake", "Warm chocolate cake with a molten center and vanilla cream.", new BigDecimal("8.00"), FoodCategory.DESSERTS, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=900&q=80", true),
            new FoodItem(6L, "Citrus Mint Cooler", "Fresh lime, orange, mint and sparkling water over ice.", new BigDecimal("5.00"), FoodCategory.DRINKS, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=900&q=80", true)
    );

    public List<FoodItem> findAll() {
        return menu;
    }

    public Optional<FoodItem> findById(Long id) {
        return menu.stream().filter(item -> item.id().equals(id)).findFirst();
    }
}