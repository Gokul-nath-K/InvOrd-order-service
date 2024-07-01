package com.quinbay.orders.exception;

import com.quinbay.orders.service.OrderService;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}
