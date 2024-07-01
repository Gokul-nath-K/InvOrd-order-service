package com.quinbay.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private String code;
    private Double totalPrice;
    private long numberOfItems;
    private Long totalQuantity;
    private Date orderedOn;
    private long customerId;
    private List<OrderItemsDto> orderItems;
}