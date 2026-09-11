package com.quickbite.foodordering.service;

import com.quickbite.foodordering.dto.CreateOrderRequest;
import com.quickbite.foodordering.dto.OrderItemResponse;
import com.quickbite.foodordering.dto.OrderResponse;
import com.quickbite.foodordering.model.FoodItem;
import com.quickbite.foodordering.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OrderService.class);

    private static final String STATUS_FIELD = "status";

    private final FoodService foodService;
    private final RestClient restClient = RestClient.create();
    private final URI functionUri;
    private final Map<String, OrderResponse> orders =
            new ConcurrentHashMap<>();

    public OrderService(
            FoodService foodService,
            @Value("${food-ordering.function-url:http://localhost:7071/api/process-order}")
            String functionUrl,
            @Value("${food-ordering.function-key:}")
            String functionKey) {

        this.foodService = foodService;
        this.functionUri = buildFunctionUri(functionUrl, functionKey);

        LOGGER.info(
                "Order processor configured at {}",
                functionUri.getScheme()
                        + "://"
                        + functionUri.getAuthority()
                        + functionUri.getPath()
        );
    }

    public OrderResponse placeOrder(CreateOrderRequest request) {

        List<OrderItemResponse> items = request.items()
                .stream()
                .map(item -> {

                    FoodItem food = foodService.getFood(item.foodId());

                    BigDecimal lineTotal =
                            food.price()
                                    .multiply(
                                            BigDecimal.valueOf(item.quantity())
                                    );

                    return new OrderItemResponse(
                            food.id(),
                            food.name(),
                            item.quantity(),
                            food.price(),
                            lineTotal
                    );
                })
                .toList();

        BigDecimal total = items.stream()
                .map(OrderItemResponse::lineTotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        /*
         * Payload sent from Spring Boot
         * to Azure Function.
         */
        Map<String, Object> functionRequest = Map.of(
                "customerName", request.customerName(),
                "email", request.email(),
                "phone", request.phone(),
                "address", request.address(),
                "items", items,
                "totalAmount", total
        );

        Map<?, ?> functionResponse;

        try {

            functionResponse = restClient.post()
                    .uri(functionUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(functionRequest)
                    .retrieve()
                    .body(Map.class);

        } catch (RestClientResponseException exception) {

            LOGGER.error(
                    "Order processor returned HTTP {}: {}",
                    exception.getStatusCode().value(),
                    exception.getResponseBodyAsString()
            );

            throw new IllegalStateException(
                    "Order processor rejected the order",
                    exception
            );

        } catch (RestClientException exception) {

            LOGGER.error(
                    "Could not reach order processor at {}",
                    functionUri,
                    exception
            );

            throw new IllegalStateException(
                    "Order processor is unavailable",
                    exception
            );
        }

        /*
         * Validate Function response.
         */
        if (functionResponse == null
                || functionResponse.get("orderId") == null
                || functionResponse.get(STATUS_FIELD) == null
                || functionResponse.get("message") == null) {

            LOGGER.error(
                    "Order processor returned an incomplete response: {}",
                    functionResponse
            );

            throw new IllegalStateException(
                    "Order processor returned an invalid response"
            );
        }

        final OrderStatus status;

        try {

            status = OrderStatus.valueOf(
                    String.valueOf(
                            functionResponse.get(STATUS_FIELD)
                    )
            );

        } catch (IllegalArgumentException exception) {

            LOGGER.error(
                    "Order processor returned an unknown status: {}",
                    functionResponse.get(STATUS_FIELD)
            );

            throw new IllegalStateException(
                    "Order processor returned an invalid status",
                    exception
            );
        }

        OrderResponse response = new OrderResponse(
                String.valueOf(
                        functionResponse.get("orderId")
                ),
                request.customerName(),
                request.email(),
                request.phone(),
                request.address(),
                items,
                total,
                status,
                String.valueOf(
                        functionResponse.get("message")
                )
        );

        orders.put(
                response.orderId(),
                response
        );

        return response;
    }

    public OrderResponse findOrder(String orderId) {

        OrderResponse response = orders.get(orderId);

        if (response == null) {

            throw new IllegalArgumentException(
                    "Order not found: " + orderId
            );
        }

        return response;
    }

    private URI buildFunctionUri(
            String functionUrl,
            String functionKey) {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(functionUrl);

        if (functionKey != null && !functionKey.isBlank()) {

            builder.queryParam(
                    "code",
                    functionKey
            );
        }

        return builder
                .build()
                .encode()
                .toUri();
    }
}