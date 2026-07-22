package com.aiecommerce.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "userId is required")
    private String userId;

    @Valid
    @NotEmpty(message = "items must not be empty")
    private List<OrderItemRequest> items;
}
