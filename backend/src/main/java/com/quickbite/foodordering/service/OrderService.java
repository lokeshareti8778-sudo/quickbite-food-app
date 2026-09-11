package com.quickbite.foodordering.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.foodordering.dto.CreateOrderRequest;
import com.quickbite.foodordering.dto.OrderItemResponse;
import com.quickbite.foodordering.dto.OrderResponse;
import com.quickbite.foodordering.model.FoodItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    private final FoodService foodService;
    private final RestClient restClient;
    private final Map<String, OrderResponse> orders = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public OrderService(FoodService foodService, ObjectMapper objectMapper,
                        @Value("${food-ordering.function-url:http://localhost:7071/api/process-order}") String functionUrl) {
        this.foodService = foodService;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().baseUrl(functionUrl).build();
    }

    public OrderResponse placeOrder(CreateOrderRequest request) {
        List<OrderItemResponse> items = request.items().stream().map(item -> {
            FoodItem food = foodService.getFood(item.foodId());
            BigDecimal lineTotal = food.price().multiply(BigDecimal.valueOf(item.quantity()));
            return new OrderItemResponse(food.id(), food.name(), item.quantity(), food.price(), lineTotal);
        }).toList();
        BigDecimal total = items.stream().map(OrderItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> functionRequest = Map.of(
                "customerName", request.customerName(),
                "email", request.email(),
                "phone", request.phone(),
                "address", request.address(),
                "items", items,
                "total", total
        );
        Map<?, ?> functionResponse = restClient.post().contentType(MediaType.APPLICATION_JSON)
                .body(functionRequest).retrieve().body(Map.class);
        if (functionResponse == null || functionResponse.get("orderId") == null) {
            throw new IllegalStateException("Order processor returned an invalid response");
        }

        OrderResponse response = new OrderResponse(
                String.valueOf(functionResponse.get("orderId")), request.customerName(), request.email(),
                request.phone(), request.address(), items, total,
                com.quickbite.foodordering.model.OrderStatus.valueOf(String.valueOf(functionResponse.get("status"))),
                String.valueOf(functionResponse.get("message"))
        );
        orders.put(response.orderId(), response);
        return response;
    }

    public OrderResponse findOrder(String orderId) {
        OrderResponse response = orders.get(orderId);
        if (response == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return response;
    }
}