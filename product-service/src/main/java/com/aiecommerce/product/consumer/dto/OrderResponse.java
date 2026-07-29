package com.aiecommerce.product.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String id;
    private String status;
    private String userId;
    private long totalAmount;

}
