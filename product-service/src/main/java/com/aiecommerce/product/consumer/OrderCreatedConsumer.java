package com.aiecommerce.product.consumer;

import com.aiecommerce.product.consumer.dto.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedConsumer {
    @KafkaListener(topics = "order-created")
    public void handleOrderCreatedEvent(String orderString) throws JsonProcessingException {
        log.info("Order created event created");
        ObjectMapper  objectMapper = new ObjectMapper();
        OrderResponse order = objectMapper.readValue(orderString, OrderResponse.class);
        log.info("Order created event received: {}", order);
    }
}
