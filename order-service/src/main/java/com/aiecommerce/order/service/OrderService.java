package com.aiecommerce.order.service;

import com.aiecommerce.order.dto.BaseResponse;
import com.aiecommerce.order.dto.CreateOrderRequest;
import com.aiecommerce.order.dto.OrderResponse;
import com.aiecommerce.order.dto.UpdateOrderRequest;

import java.util.List;

public interface OrderService {

    BaseResponse<OrderResponse> create(CreateOrderRequest request);

    BaseResponse<List<OrderResponse>> getAll();

    BaseResponse<OrderResponse> getById(String id);

    BaseResponse<OrderResponse> update(String id, UpdateOrderRequest request);

    BaseResponse<Void> delete(String id);
}
