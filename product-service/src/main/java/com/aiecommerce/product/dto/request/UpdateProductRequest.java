package com.aiecommerce.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
    @NotEmpty
    private String name;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private Integer stock;

    @NotNull
    private String categoryId;
}
