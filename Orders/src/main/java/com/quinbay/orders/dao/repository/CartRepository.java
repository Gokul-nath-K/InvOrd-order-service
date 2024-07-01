package com.quinbay.orders.dao.repository;

import com.quinbay.orders.dao.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {

    Cart findByCode(String cartId);
}