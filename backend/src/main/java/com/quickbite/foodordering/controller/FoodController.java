package com.quickbite.foodordering.controller;

import com.quickbite.foodordering.model.FoodItem;
import com.quickbite.foodordering.service.FoodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public List<FoodItem> getFoods() {
        return foodService.getMenu();
    }

    @GetMapping("/{id}")
    public FoodItem getFood(@PathVariable Long id) {
        return foodService.getFood(id);
    }
}