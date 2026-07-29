package com.aiecommerce.product.consumer.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@RequiredArgsConstructor
public class OrderCreatedEvent extends OrderResponse{
    List<OrderItemResponse> orderItems;
}
