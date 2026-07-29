package com.aiecommerce.order.consumer;

import com.aiecommerce.order.dto.response.OrderItemResponse;
import com.aiecommerce.order.dto.response.OrderResponse;
import com.aiecommerce.order.entity.OrderStatus;
import com.aiecommerce.order.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ProductLockedConsumer {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    @KafkaListener(topics = "product-locked")
    @Retryable(
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void handleProductLockedEvent(String orderString) throws JsonProcessingException {
        OrderResponse response = objectMapper.readValue(orderString, OrderResponse.class);
        orderService.changeStatus(response.getId(), String.valueOf(OrderStatus.CONFIRMED));
    }
}
