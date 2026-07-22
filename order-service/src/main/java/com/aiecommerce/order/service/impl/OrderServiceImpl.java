package com.aiecommerce.order.service.impl;

import com.aiecommerce.order.client.ProductClient;
import com.aiecommerce.order.dto.BaseResponse;
import com.aiecommerce.order.dto.request.CreateOrderRequest;
import com.aiecommerce.order.dto.request.OrderItemRequest;
import com.aiecommerce.order.dto.request.ProductFilter;
import com.aiecommerce.order.dto.request.UpdateOrderRequest;
import com.aiecommerce.order.dto.response.OrderResponse;
import com.aiecommerce.order.dto.response.ProductDto;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;

    @Override
    @Transactional
    public BaseResponse<OrderResponse> create(CreateOrderRequest request) {
        // 1. Lấy danh sách productId không trùng từ request
        List<String> productIds = request.getItems().stream()
                .map(OrderItemRequest::getProductId)
                .distinct()
                .toList();

        // 2. Gọi product-service lấy thông tin sản phẩm một lần
        List<ProductDto> products = productClient.getProductByIds(new ProductFilter(productIds)); // productIds khớp với field trong product-service

        // 3. Build map productId -> ProductDto để lookup O(1)
        Map<String, ProductDto> productMap = products.stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

        // 4. Map request sang Order entity
        Order order = orderMapper.fromRequest(request);
        order.setStatus(OrderStatus.CREATED);

        // 5. Xử lý từng item
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductDto product = productMap.get(itemRequest.getProductId());
            if (product == null) {
                throw new BadRequestException("Product not found: " + itemRequest.getProductId());
            }
            validateStock(product, itemRequest.getQuantity());

            OrderItem item = orderMapper.fromItemRequest(itemRequest);
            item.setUnitPrice(product.getPrice());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            item.setLineTotal(lineTotal);

            order.addItem(item);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setIsDeleted(false);
        Order savedOrder = orderRepository.save(order);
        return BaseResponse.success(orderMapper.toResponse(savedOrder), "Order created successfully");
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

    private void validateStock(ProductDto product, Integer quantity) {
        if (product.getStock() == null) {
            throw new BadRequestException("Stock is not available for product " + product.getId());
        }
        if (quantity > product.getStock()) {
            throw new BadRequestException(
                    "Insufficient stock for product " + product.getId() + ", available: " + product.getStock());
        }
    }
}
