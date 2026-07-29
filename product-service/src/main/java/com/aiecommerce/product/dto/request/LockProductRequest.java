package com.aiecommerce.product.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class LockProductRequest {
    @NotEmpty
    private List<LockProductItem> items;
}
