package com.quinbay.orders.service.implementation;

import com.google.gson.JsonSerializer;
import com.quinbay.orders.config.ProducerConfiguration;
import com.quinbay.orders.dto.ProductDTO;
import com.quinbay.orders.dto.ProductRequest;
import com.quinbay.orders.exception.CartException;
import com.quinbay.orders.dao.entity.Cart;
import com.quinbay.orders.dao.repository.CartRepository;
import com.quinbay.orders.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final RestTemplate restTemplate;

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Cart getCartById(String cartId) {

        return Optional.ofNullable(cartRepository.findByCode(cartId)).orElseThrow(() -> new CartException("Cart not found with ID: " + cartId));
    }

    @Override
    public String createCart() {
        Cart cart = Cart.builder()
                .code("CART_" + new Random().nextInt(1, 100))
                .products(new ArrayList<>())
                .build();

        cartRepository.save(cart);
        return "Cart created with id: " + cart.getCode();
    }

    @Override
    public String addProductToCart(String cartId, ProductRequest productRequest) {
        if (productRequest.getQuantity() <= 0) {
            throw new CartException("Quantity cannot be zero or negative");
        }
        ResponseEntity<ProductDTO> response = null;
        try{
                response = restTemplate.getForEntity(
                "http://localhost:8081/product/getByCode?id=" + productRequest.getCode(),
                ProductDTO.class
        );
        }catch (Exception e){
            throw new CartException("Product not found! Can't add product to cart.");
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            ProductDTO fetchedProduct = response.getBody();
            if (fetchedProduct.getQuantity() < productRequest.getQuantity()) {
                throw new CartException("Not enough quantity available for product " + fetchedProduct.getName());
            }

            Cart cart = getCartById(cartId);
            List<ProductDTO> productList = cart.getProducts();
            boolean productExists = false;

            for (ProductDTO existingProduct : productList) {
                if (existingProduct.getCode().equals(productRequest.getCode())) {
                    long totalQuantity = existingProduct.getQuantity() + productRequest.getQuantity();
                    if (totalQuantity > fetchedProduct.getQuantity()) {
                        throw new CartException("Total quantity exceeds available quantity for product " + fetchedProduct.getName());
                    }
                    existingProduct.setQuantity(totalQuantity);
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                fetchedProduct.setQuantity(productRequest.getQuantity());
                productList.add(fetchedProduct);
            }

            cart.setProducts(productList);
            updateCart(cart);

            return "Product added to cart successfully";
        } else {
            throw new CartException("Product not found with ID: " + productRequest.getCode());
        }
    }

    @Override
    public String updateCart(Cart cart) {
        cartRepository.save(cart);
        return "Cart updated successfully";
    }

    @Override
    public void clearCart(String cartId) {
        Cart cart = getCartById(cartId);
        cart.setProducts(new ArrayList<>());
        cartRepository.save(cart);
    }
}
