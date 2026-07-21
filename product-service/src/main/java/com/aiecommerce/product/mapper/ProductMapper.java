package com.aiecommerce.product.mapper;

import com.aiecommerce.product.dto.request.CreateProductRequest;
import com.aiecommerce.product.dto.response.ReturnProductResponse;
import com.aiecommerce.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.Target;


@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    Product fromRequest(CreateProductRequest request);
    ReturnProductResponse toResponse(Product product);
}
