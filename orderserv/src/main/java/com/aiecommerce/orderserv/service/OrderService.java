package com.aiecommerce.orderserv.service;

import com.aiecommerce.orderserv.client.ProductClient;
import com.aiecommerce.orderserv.client.ProductDto;
import com.aiecommerce.orderserv.dto.CreateOrderRequest;
import com.aiecommerce.orderserv.dto.OrderItemRequest;
import com.aiecommerce.orderserv.dto.OrderItemResponse;
import com.aiecommerce.orderserv.dto.OrderResponse;
import com.aiecommerce.orderserv.entity.Order;
import com.aiecommerce.orderserv.entity.OrderItem;
import com.aiecommerce.orderserv.entity.OrderStatus;
import com.aiecommerce.orderserv.exception.BadRequestException;
import com.aiecommerce.orderserv.exception.ResourceNotFoundException;
import com.aiecommerce.orderserv.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductDto product = productClient.getById(itemRequest.getProductId());
            validateStock(product, itemRequest.getQuantity());

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(product.getPrice());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setLineTotal(lineTotal);

            order.addItem(item);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        return toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        return toResponse(order);
    }

    private void validateStock(ProductDto product, Integer quantity) {
        if (product.getStock() == null) {
            return;
        }
        if (quantity > product.getStock()) {
            throw new BadRequestException(
                    "Insufficient stock for product " + product.getId() + ", available: " + product.getStock());
        }
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProductId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setLineTotal(item.getLineTotal());
            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);
        return response;
    }
}
