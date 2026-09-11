package com.quickbite.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Map;
import java.util.Optional;

public class ProcessOrderFunction {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderProcessor orderProcessor = new OrderProcessor(objectMapper);

    @FunctionName("process-order")
    public HttpResponseMessage run(
            @HttpTrigger(name = "request", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        try {
                String body = request.getBody().orElse("");
            context.getLogger().info("Received order request body length: "
                    + body.length());
            Map<String, String> response = orderProcessor.process(body);
            return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(response).build();
        } catch (IllegalArgumentException exception) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(Map.of("error", exception.getMessage())).build();
        } catch (Exception exception) {
            context.getLogger().severe("Order processing failed: " + exception.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Order could not be processed")).build();
        }
    }
}