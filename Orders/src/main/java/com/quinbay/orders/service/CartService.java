package com.quinbay.orders.service;

import com.quinbay.orders.dto.ProductRequest;
import com.quinbay.orders.dao.entity.Cart;

import java.util.List;

public interface CartService {

    List<Cart> getAllCarts();

    Cart getCartById(String cartId);

    String createCart();

    String addProductToCart(String cartId, ProductRequest productRequest);

    String updateCart(Cart cart);

    void clearCart(String cartId);
}
