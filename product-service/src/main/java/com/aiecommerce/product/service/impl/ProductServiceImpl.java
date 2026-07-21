package com.aiecommerce.product.service.impl;

import com.aiecommerce.product.dto.BaseResponse;
import com.aiecommerce.product.dto.request.CreateProductRequest;
import com.aiecommerce.product.dto.response.ReturnProductResponse;
import com.aiecommerce.product.entity.Product;
import com.aiecommerce.product.exception.ApplicationException;
import com.aiecommerce.product.mapper.ProductMapper;
import com.aiecommerce.product.repository.CategoryRepository;
import com.aiecommerce.product.repository.ProductRepository;
import com.aiecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    @Override
    public BaseResponse<ReturnProductResponse> create(CreateProductRequest request) {
        var existedCategory = categoryRepository.findById(request.getCategoryId());
        if(existedCategory.isEmpty()){
            throw new ApplicationException("Category not found");
        }
        Product product = productMapper.fromRequest(request);
        product.setCategory(existedCategory.get());
        productRepository.save(product);
        return BaseResponse.success(productMapper.toResponse(product), "Create product successfully");
    }

    public BaseResponse<List<ReturnProductResponse>> getAll(){
        List<Product> products = productRepository.findAll();
        List<ReturnProductResponse> returnProductResponses = products
                .stream()
                .map(productMapper::toResponse).toList();
        return BaseResponse.success(returnProductResponses, "Get all products successfully");
    }
}
