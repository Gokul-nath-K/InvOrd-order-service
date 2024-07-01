package com.quinbay.orders.dao.repository;

import com.quinbay.orders.dao.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
    Order findByCode(String code);
}
