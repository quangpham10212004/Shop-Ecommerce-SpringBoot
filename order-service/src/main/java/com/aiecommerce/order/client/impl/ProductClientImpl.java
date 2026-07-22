package com.aiecommerce.order.client.impl;

import com.aiecommerce.order.client.ProductClient;
import com.aiecommerce.order.dto.BaseResponse;
import com.aiecommerce.order.dto.request.ProductFilter;
import com.aiecommerce.order.dto.response.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<ProductDto> getProductByIds(ProductFilter productFilter) {
        BaseResponse<List<ProductDto>> response = webClientBuilder.build()
                .post()
                .uri( "http://localhost:8888/v1/products/search")
                .bodyValue(productFilter)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<BaseResponse<List<ProductDto>>>() {
                })
                .block();
        if(response == null ){
            throw new RuntimeException("Products not found");
        }
        return response.data();

    }
}
