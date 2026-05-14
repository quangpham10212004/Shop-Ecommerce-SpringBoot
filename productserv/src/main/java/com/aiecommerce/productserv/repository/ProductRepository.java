package com.aiecommerce.productserv.repository;

import com.aiecommerce.productserv.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
