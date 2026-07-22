package com.aiecommerce.order.client;

import com.aiecommerce.order.dto.request.ProductFilter;
import com.aiecommerce.order.dto.response.ProductDto;

import java.util.List;

public interface ProductClient {
    List<ProductDto> getProductByIds (ProductFilter productFilter);
}
