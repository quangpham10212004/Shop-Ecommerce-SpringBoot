package com.aiecommerce.order.service.impl;

import com.aiecommerce.order.dto.BaseResponse;
import com.aiecommerce.order.dto.request.CreateOrderRequest;
import com.aiecommerce.order.dto.request.OrderItemRequest;
import com.aiecommerce.order.dto.response.OrderResponse;
import com.aiecommerce.order.dto.request.UpdateOrderRequest;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public BaseResponse<OrderResponse> create(CreateOrderRequest request) {
        Order order = orderMapper.fromRequest(request);
        order.setStatus(OrderStatus.CREATED);


        return BaseResponse.success(orderMapper.toResponse(null), "Order created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<OrderResponse>> getAll() {
        List<OrderResponse> responses = orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
        return BaseResponse.success(responses, "Get all orders successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<OrderResponse> getById(String id) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        return BaseResponse.success(orderMapper.toResponse(order));
    }

    @Override
    @Transactional
    public BaseResponse<OrderResponse> update(String id, UpdateOrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        return BaseResponse.success(orderMapper.toResponse(order), "Update order successfully");
    }

    @Override
    @Transactional
    public BaseResponse<Void> delete(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " not found"));
        orderRepository.delete(order);
        return BaseResponse.success(null, "Delete order successfully");
    }

//    private void validateStock(ProductDto product, Integer quantity) {
//        if (product.getStock() == null) {
//            return;
//        }
//        if (quantity > product.getStock()) {
//            throw new BadRequestException(
//                    "Insufficient stock for product " + product.getId() + ", available: " + product.getStock());
//        }
//    }
}
