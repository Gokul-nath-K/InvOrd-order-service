package com.quinbay.orders.controller;

import com.quinbay.orders.dto.ProductRequest;
import com.quinbay.orders.dao.entity.Cart;
import com.quinbay.orders.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> carts = cartService.getAllCarts();
        return ResponseEntity.ok(carts);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable String cartId) {
        Cart cart = cartService.getCartById(cartId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<String> createCart() {
        String message = cartService.createCart();
        return ResponseEntity.ok(message);
    }

    @PostMapping("/{cartId}/addProduct")
    public ResponseEntity<String> addProductToCart(@PathVariable String cartId, @RequestBody ProductRequest productRequest) {
        String message = cartService.addProductToCart(cartId, productRequest);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> clearCart(@PathVariable String cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
