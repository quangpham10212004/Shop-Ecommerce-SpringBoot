package com.aiecommerce.productserv.service;

import com.aiecommerce.productserv.dto.ProductRequest;
import com.aiecommerce.productserv.entity.Product;
import com.aiecommerce.productserv.exception.ResourceNotFoundException;
import com.aiecommerce.productserv.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(request.getCategoryId());
        product.setVersion(0);
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product update(Long id, ProductRequest request) {
        Product existing = getById(id);
        existing.setName(request.getName());
        existing.setSku(request.getSku());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategoryId(request.getCategoryId());
        existing.setVersion(existing.getVersion() + 1);
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        Product existing = getById(id);
        productRepository.delete(existing);
    }
}
