package com.aiecommerce.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LockProductItem {
    @NotEmpty
    private String id;

    @NotNull
    @Positive
    private int quantity;
}
