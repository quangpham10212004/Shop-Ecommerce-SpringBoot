package com.aiecommerce.order.service;

import com.aiecommerce.order.dto.BaseResponse;
import com.aiecommerce.order.dto.CreateOrderRequest;
import com.aiecommerce.order.dto.OrderResponse;

public interface OrderService {

    BaseResponse<OrderResponse> create(CreateOrderRequest request);

    BaseResponse<OrderResponse> getById(String id);
}
