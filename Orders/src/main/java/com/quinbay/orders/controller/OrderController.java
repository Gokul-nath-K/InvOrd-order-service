package com.quinbay.orders.controller;

import com.quinbay.orders.dao.entity.Order;
import com.quinbay.orders.dto.MessageDto;
import com.quinbay.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    private KafkaTemplate<String, MessageDto> kafkaTemplate;


    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/placeOrder/{cartId}")
    public ResponseEntity<String> placeOrder(@PathVariable String cartId) {
        orderService.placeOrder(cartId);

        kafkaTemplate.send("message.service", MessageDto.builder()
                        .recipient("cseskct05gokulnath.k@gmail.com")
                        .subject("Order status from kafka")
                        .body("Order place successfully")
                .build());

        return ResponseEntity.ok("Order placed successfully");
    }
}
