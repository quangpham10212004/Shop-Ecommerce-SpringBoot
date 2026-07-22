package com.aiecommerce.order.dto.response;

import com.aiecommerce.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String id;
    private String userId;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
}
