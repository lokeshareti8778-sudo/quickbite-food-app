package com.quickbite.foodordering.service;

import com.quickbite.foodordering.model.FoodItem;
import com.quickbite.foodordering.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public List<FoodItem> getMenu() {
        return foodRepository.findAll();
    }

    public FoodItem getFood(Long id) {
        return foodRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Food item not found: " + id));
    }
}