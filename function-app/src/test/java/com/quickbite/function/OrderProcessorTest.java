package com.quickbite.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderProcessorTest {
        private static final String VALID_PAYLOAD = """
                        {
                            "totalAmount": 7.50,
                            "address": "bhimavaram gp",
                            "email": "lokeshareti6555@gmail.com",
                            "customerName": "Lokesh ARETI",
                            "items": [
                                {
                                    "foodId": 4,
                                    "name": "Truffle Parmesan Fries",
                                    "quantity": 1,
                                    "unitPrice": 7.50,
                                    "lineTotal": 7.50
                                }
                            ],
                            "phone": "91 9000000000"
                        }
                        """;
    private final OrderProcessor processor = new OrderProcessor(new ObjectMapper());

    @Test
    void confirmsAValidOrder() throws Exception {
        var response = processor.process("{\"customerName\":\"Alex\",\"items\":[{\"foodId\":1}]} ");
        assertTrue(response.get("orderId").startsWith("ORD-"));
        assertEquals("CONFIRMED", response.get("status"));
        assertEquals("Order placed successfully", response.get("message"));
    }

    @Test
    void acceptsTheExactSpringBootOrderPayload() throws Exception {
        var response = processor.process(VALID_PAYLOAD);

        assertEquals("CONFIRMED", response.get("status"));
        assertEquals("Order placed successfully", response.get("message"));
    }

    @Test
    void mapsTheCompleteSpringBootPayloadToTheRequestDto() throws Exception {
        var request = new ObjectMapper().readValue(VALID_PAYLOAD, OrderRequest.class);
        var item = request.items().get(0);

        assertEquals("Lokesh ARETI", request.customerName());
        assertEquals("lokeshareti6555@gmail.com", request.email());
        assertEquals("91 9000000000", request.phone());
        assertEquals("bhimavaram gp", request.address());
        assertEquals(new BigDecimal("7.50"), request.totalAmount());
        assertNotNull(request.items());
        assertEquals(1, request.items().size());
        assertEquals(4L, item.foodId());
        assertEquals("Truffle Parmesan Fries", item.name());
        assertEquals(1, item.quantity());
        assertEquals(new BigDecimal("7.50"), item.unitPrice());
        assertEquals(new BigDecimal("7.50"), item.lineTotal());
    }

    @Test
    void rejectsAnOrderWithoutCustomerName() {
        assertThrows(IllegalArgumentException.class,
                () -> processor.process("{\"items\":[{\"foodId\":1}]}"));
    }

    @Test
    void acceptsValidEmail() throws Exception {
        assertEquals("CONFIRMED", processor.process(VALID_PAYLOAD).get("status"));
    }

    @Test
    void acceptsValidPhone() throws Exception {
        assertEquals("CONFIRMED", processor.process(VALID_PAYLOAD).get("status"));
    }

    @Test
    void acceptsValidAddress() throws Exception {
        assertEquals("CONFIRMED", processor.process(VALID_PAYLOAD).get("status"));
    }

    @Test
    void acceptsValidTotalAmount() throws Exception {
        assertEquals("CONFIRMED", processor.process(VALID_PAYLOAD).get("status"));
    }

    @Test
    void acceptsValidItems() throws Exception {
        assertEquals("CONFIRMED", processor.process(VALID_PAYLOAD).get("status"));
    }

    @Test
    void rejectsAnOrderWithoutItems() {
        assertThrows(IllegalArgumentException.class, () -> processor.process("{\"customerName\":\"Alex\",\"items\":[]}"));
    }

}