package com.quickbite.foodordering.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name is required") String customerName,
        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") String email,
        @NotBlank(message = "Phone number is required") @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "Enter a valid phone number") String phone,
        @NotBlank(message = "Delivery address is required") String address,
        @NotEmpty(message = "Add at least one item") @Valid List<OrderItemRequest> items
) {
}