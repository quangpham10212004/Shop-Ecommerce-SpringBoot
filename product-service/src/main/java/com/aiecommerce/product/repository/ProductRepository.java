package com.aiecommerce.product.repository;

import com.aiecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    public List<Product> findByIdIsIn(List<String> ids);
}
