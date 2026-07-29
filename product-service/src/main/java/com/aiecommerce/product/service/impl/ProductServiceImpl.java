package com.aiecommerce.product.service.impl;

import com.aiecommerce.product.dto.BaseResponse;
import com.aiecommerce.product.dto.request.*;
import com.aiecommerce.product.dto.response.ReturnProductResponse;
import com.aiecommerce.product.entity.Product;
import com.aiecommerce.product.exception.ApplicationException;
import com.aiecommerce.product.exception.ResourceNotFoundException;
import com.aiecommerce.product.mapper.ProductMapper;
import com.aiecommerce.product.repository.CategoryRepository;
import com.aiecommerce.product.repository.ProductRepository;
import com.aiecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public BaseResponse<ReturnProductResponse> create(CreateProductRequest request) {
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApplicationException("Category not found"));
        Product product = productMapper.fromRequest(request);
        product.setCategory(category);
        product.setIsDeleted(false);
        productRepository.save(product);

        return BaseResponse.success(productMapper.toResponse(product), "Create product successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<ReturnProductResponse>> getAll() {
        List<ReturnProductResponse> responses = productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
        return BaseResponse.success(responses, "Get all products successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<ReturnProductResponse> getById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return BaseResponse.success(productMapper.toResponse(product));
    }

    @Override
    @Transactional
    public BaseResponse<ReturnProductResponse> update(String id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ApplicationException("Category not found"));
        productMapper.updateFromRequest(request, product);
        product.setCategory(category);
        productRepository.save(product);
        return BaseResponse.success(productMapper.toResponse(product), "Update product successfully");
    }



    @Override
    @Transactional
    public BaseResponse<Void> delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);
        return BaseResponse.success(null, "Delete product successfully");
    }

    @Override
    public BaseResponse<List<ReturnProductResponse>> search(ProductFilter filter) {
        List<ReturnProductResponse> list = productRepository.findByIdIsIn(filter.getProductIds())
                .stream().map(productMapper::toResponse).toList();
        return BaseResponse.success(list, "Search product successfully");
    }



    @Override
    @Transactional
    public BaseResponse<Void> lockStock(LockProductRequest req) {
        List<LockProductItem> items = req.getItems();
        var productIdQuantityMap = items.stream().collect(Collectors.toMap(LockProductItem::getId, LockProductItem::getQuantity));
        List<Product > products = productRepository.findByIdIsIn(new ArrayList<>(productIdQuantityMap.keySet()));
        products.forEach(product -> {
            int quantity = productIdQuantityMap.get(product.getId());
            int stock = product.getStock();
            int updatedStock = stock - quantity;
            product.setStock(updatedStock);
        });
        productRepository.saveAll(products);
        return BaseResponse.success(null, "Lock stock successfully");
    }


}
