package com.aiecommerce.orderserv.repository;

import com.aiecommerce.orderserv.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "items")
    java.util.Optional<Order> findWithItemsById(Long id);
}
