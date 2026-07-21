package com.aiecommerce.product.service;

import com.aiecommerce.product.dto.BaseResponse;
import com.aiecommerce.product.dto.request.CreateProductRequest;
import com.aiecommerce.product.dto.request.UpdateProductRequest;
import com.aiecommerce.product.dto.response.ReturnProductResponse;

import java.util.List;

public interface ProductService {
    BaseResponse<ReturnProductResponse> create(CreateProductRequest request);
    
    BaseResponse<List<ReturnProductResponse>> getAll();
    
    BaseResponse<ReturnProductResponse> getById(String id);
    
    BaseResponse<ReturnProductResponse> update(String id, UpdateProductRequest request);
    
    BaseResponse<Void> delete(String id);
}
