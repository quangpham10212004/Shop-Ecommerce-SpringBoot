package com.aiecommerce.order.client;

import com.aiecommerce.order.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", configuration = FeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/v1/products/{id}")
    ProductDto getById(@PathVariable("id") String id);

    @GetMapping("/v1/products")
    List<ProductDto> getAll();
}
