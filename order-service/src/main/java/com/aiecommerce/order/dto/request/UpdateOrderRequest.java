package com.aiecommerce.order.dto.request;

import com.aiecommerce.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderRequest {

    @NotNull(message = "status is required")
    private OrderStatus status;
}
