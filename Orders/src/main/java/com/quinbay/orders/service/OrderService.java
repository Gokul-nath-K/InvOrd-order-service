package com.quinbay.orders.service;

import com.quinbay.orders.dao.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getAllOrders();

    Order getOrderById(String id);

    void placeOrder(String cartId);
}
