package com.aiecommerce.orderserv.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "productserv")
public interface ProductClient {

    @GetMapping("/v1/products/{id}")
    ProductDto getById(@PathVariable("id") Long id);
}
