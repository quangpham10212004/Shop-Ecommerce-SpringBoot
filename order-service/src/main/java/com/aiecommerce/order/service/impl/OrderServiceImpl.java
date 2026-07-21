package com.aiecommerce.order.service.impl;

import com.aiecommerce.order.client.ProductClient;
import com.aiecommerce.order.client.ProductDto;
import com.aiecommerce.order.dto.BaseResponse;
import com.aiecommerce.order.dto.CreateOrderRequest;
import com.aiecommerce.order.dto.OrderItemRequest;
import com.aiecommerce.order.dto.OrderResponse;
import com.aiecommerce.order.entity.Order;
import com.aiecommerce.order.entity.OrderItem;
import com.aiecommerce.order.entity.OrderStatus;
import com.aiecommerce.order.exception.BadRequestException;
import com.aiecommerce.order.exception.ResourceNotFoundException;
import com.aiecommerce.order.mapper.OrderMapper;
import com.aiecommerce.order.repository.OrderRepository;
import com.aiecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public BaseResponse<OrderResponse> create(CreateOrderRequest request) {
        Order order = orderMapper.fromRequest(request);
        order.setStatus(OrderStatus.CREATED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductDto product = productClient.getById(itemRequest.getProductId());
            validateStock(product, itemRequest.getQuantity());

            OrderItem item = orderMapper.fromItemRequest(itemRequest);
            item.setUnitPrice(product.getPrice());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setLineTotal(lineTotal);

            order.addItem(item);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        return BaseResponse.success(orderMapper.toResponse(savedOrder), "Order created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<OrderResponse> getById(String id) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        return BaseResponse.success(orderMapper.toResponse(order));
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
}
