package com.aiecommerce.product.consumer.dto;

import java.math.BigDecimal;

@lombok.Getter
@lombok.Setter
public class OrderItemResponse {
    private String id;
    private String productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
