package com.aiecommerce.order.mapper;

import com.aiecommerce.order.dto.request.CreateOrderRequest;
import com.aiecommerce.order.dto.request.OrderItemRequest;
import com.aiecommerce.order.dto.response.OrderItemResponse;
import com.aiecommerce.order.dto.response.OrderResponse;
import com.aiecommerce.order.entity.Order;
import com.aiecommerce.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    Order fromRequest(CreateOrderRequest request);

    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    OrderResponse toResponse(Order order);

    // OrderItem: Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    OrderItem fromItemRequest(OrderItemRequest request);

    // OrderItem: Entity → Response
    OrderItemResponse toItemResponse(OrderItem item);
}
