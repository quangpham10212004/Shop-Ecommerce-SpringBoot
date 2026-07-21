package com.aiecommerce.order.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductDto {

    private String id;
    private BigDecimal price;
    private Integer stock;
}
