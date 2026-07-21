package com.aiecommerce.product.controller;

import com.aiecommerce.product.dto.BaseResponse;
import com.aiecommerce.product.dto.request.CreateProductRequest;
import com.aiecommerce.product.dto.response.ReturnProductResponse;
import com.aiecommerce.product.entity.Product;
import com.aiecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {
	
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<BaseResponse<ReturnProductResponse>> create(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ReturnProductResponse>>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

//    @GetMapping
//    public List<Product> getAll() {
//        return productService.getAll();
//    }
//
//    @GetMapping("/{id}")
//    public Product getById(@PathVariable Long id) {
//        return productService.getById(id);
//    }
//
//    @PutMapping("/{id}")
//    public Product update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
//        return productService.update(id, request);
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id) {
//        productService.delete(id);
//    }
}
