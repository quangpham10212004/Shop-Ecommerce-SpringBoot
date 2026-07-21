package com.aiecommerce.product.service;

import com.aiecommerce.product.dto.BaseResponse;
import com.aiecommerce.product.dto.request.CreateProductRequest;
import com.aiecommerce.product.dto.response.ReturnProductResponse;
import com.aiecommerce.product.entity.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    BaseResponse<ReturnProductResponse> create(CreateProductRequest request);
    BaseResponse<List<ReturnProductResponse>> getAll();
}
