package com.aiecommerce.orderserv.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.aiecommerce.orderserv.config.FeignClientConfig;

@FeignClient(name = "productserv", configuration = FeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/v1/products/{id}")
    ProductDto getById(@PathVariable("id") Long id);
    @GetMapping("/v1/products")
    List<ProductDto> getAll();
}
