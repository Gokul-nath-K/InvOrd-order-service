package com.quinbay.orders.service.implementation;

import com.quinbay.orders.dto.OrderDto;
import com.quinbay.orders.dto.OrderItemsDto;
import com.quinbay.orders.dto.ProductDTO;
import com.quinbay.orders.exception.OrderException;
import com.quinbay.orders.dao.entity.Cart;
import com.quinbay.orders.dao.entity.Order;
import com.quinbay.orders.dao.repository.OrderRepository;
import com.quinbay.orders.service.CartService;
import com.quinbay.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartService cartService;

    private final RestTemplate restTemplate;

    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(String id) {
        Optional<Order> order = Optional.ofNullable(orderRepository.findByCode(id));
        if (order.isPresent()) {
            return order.get();
        } else {
            throw new OrderException("Order not found with ID: " + id);
        }
    }

    @Override
    public void placeOrder(String cartId) {
        Cart cart = cartService.getCartById(cartId);
        if (cart == null) {
            throw new OrderException("Cart not found with ID: " + cartId);
        }

        Order order = new Order();
        long totalQuantity = cart.getProducts().stream().mapToLong(ProductDTO::getQuantity).sum();
        long numberOfItems = cart.getProducts().size();
        double totalPrice = cart.getProducts().stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();

        order.setTotalQuantity(totalQuantity);
        order.setNumberOfItems(numberOfItems);
        order.setTotalPrice(totalPrice);
        order.setCode("ORD_" + new Random().nextInt(1, 100));
        order.setOrderedOn(new Date());
        order.setProducts(cart.getProducts());

        updateProductQuantities(order.getProducts());
        cartService.clearCart(cartId);

        order = orderRepository.save(order);

        List<OrderItemsDto> orderItemsList = getOrderItemsDtos(cart);

        OrderDto orderDTO = OrderDto.builder()
                .totalQuantity(order.getTotalQuantity())
                .numberOfItems(order.getNumberOfItems())
                .totalPrice(order.getTotalPrice())
                .code(order.getCode())
                .orderedOn(new Date())
                .customerId(1)
                .orderItems(orderItemsList)
                .build();

        kafkaTemplate.send("order.service", orderDTO);
    }

    private static List<OrderItemsDto> getOrderItemsDtos(Cart cart) {

        List<OrderItemsDto> orderItemsList = new ArrayList<>();
        for (ProductDTO productDTO : cart.getProducts()) {
            OrderItemsDto orderItem = new OrderItemsDto();
            orderItem.setCode(productDTO.getCode());
            orderItem.setName(productDTO.getName());
            orderItem.setQuantity(productDTO.getQuantity());
            orderItem.setPrice(productDTO.getPrice());
            orderItem.setSellerId(productDTO.getSellerId());
            orderItem.setCategoryCode(productDTO.getCategoryCode());
            orderItemsList.add(orderItem);
        }
        return orderItemsList;
    }

    List<OrderItemsDto> getOrderItems(List<ProductDTO> productDTOS) {

        List<OrderItemsDto> orderItemsDtos = new ArrayList<>();

        for(ProductDTO productDTO : productDTOS) {

            orderItemsDtos.add(OrderItemsDto.builder()
                            .code(productDTO.getCode())
                            .name(productDTO.getName())
                            .quantity(productDTO.getQuantity())
                            .sellerId(productDTO.getSellerId())
                            .categoryCode(productDTO.getCategoryCode())
                    .build());
        }
        return orderItemsDtos;
    }

    private void updateProductQuantities(List<ProductDTO> products) {
        for (ProductDTO product : products) {
            ResponseEntity<ProductDTO> response = restTemplate.getForEntity(
                    "http://localhost:8081/product/getByCode?id=" + product.getCode(),
                    ProductDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                ProductDTO fetchedProduct = response.getBody();
                long newQuantity = fetchedProduct.getQuantity() - product.getQuantity();
                fetchedProduct.setQuantity(newQuantity);

                restTemplate.put("http://localhost:8081/product/update", fetchedProduct);
            } else {
                throw new OrderException("Failed to fetch product details for ID: " + product.getCode());
            }
        }
    }
}
