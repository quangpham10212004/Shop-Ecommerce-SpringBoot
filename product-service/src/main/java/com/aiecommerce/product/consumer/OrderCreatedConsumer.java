package com.aiecommerce.product.consumer;

import com.aiecommerce.product.consumer.dto.OrderCreatedEvent;
import com.aiecommerce.product.consumer.dto.OrderResponse;
import com.aiecommerce.product.dto.request.LockProductItem;
import com.aiecommerce.product.dto.request.LockProductRequest;
import com.aiecommerce.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class OrderCreatedConsumer {
    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @KafkaListener(topics = "order-created")
    @Retryable(
            maxAttempts = 4,
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void handleOrderCreatedEvent(String orderString) throws JsonProcessingException {
        OrderCreatedEvent event = objectMapper.readValue(orderString, OrderCreatedEvent.class);
        log.info("Order created event received: {}", event);
        List<LockProductItem> items = new ArrayList<>();
        event.getOrderItems().forEach(orderItem -> {
            LockProductItem item = new LockProductItem();
            item.setId(orderItem.getProductId());
            item.setQuantity(orderItem.getQuantity());
            items.add(item);
        });
        LockProductRequest req = new LockProductRequest(items);
        productService.lockStock(req);
        log.info("Stock locked successfully");
        // day request len
        kafkaTemplate.send("product-locked", event);
        log.info("Product locked successfully");
    }
}
