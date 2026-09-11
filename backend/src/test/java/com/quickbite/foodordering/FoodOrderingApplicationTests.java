package com.quickbite.foodordering;

import com.quickbite.foodordering.repository.FoodRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoodOrderingApplicationTests {
    @Test
    void menuContainsSixFeaturedItems() {
        assertEquals(6, new FoodRepository().findAll().size());
    }
}