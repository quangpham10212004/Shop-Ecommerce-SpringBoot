package com.aiecommerce.product.service;

import com.aiecommerce.product.dto.BaseResponse;
import com.aiecommerce.product.dto.request.CreateProductRequest;
import com.aiecommerce.product.dto.request.ProductFilter;
import com.aiecommerce.product.dto.request.UpdateProductRequest;
import com.aiecommerce.product.dto.response.ReturnProductResponse;
import com.fasterxml.jackson.databind.ser.Serializers;

import java.util.List;

public interface ProductService {
    BaseResponse<ReturnProductResponse> create(CreateProductRequest request);
    
    BaseResponse<List<ReturnProductResponse>> getAll();
    
    BaseResponse<ReturnProductResponse> getById(String id);
    
    BaseResponse<ReturnProductResponse> update(String id, UpdateProductRequest request);
    
    BaseResponse<Void> delete(String id);

    BaseResponse<List<ReturnProductResponse>> search(ProductFilter filter);
    BaseResponse<ReturnProductResponse> deductStock(String id, int quantity);
}
