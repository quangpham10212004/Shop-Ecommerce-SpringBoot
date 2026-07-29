package com.aiecommerce.order.event;

import com.aiecommerce.order.dto.response.OrderItemResponse;
import com.aiecommerce.order.dto.response.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class OrderCreatedEvent extends OrderResponse {
    List<OrderItemResponse> orderItems;
}
