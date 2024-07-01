package com.quinbay.orders.dao.entity;

import com.quinbay.orders.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    @Field("code")
    private String code;

    @Field("total_price")
    private double totalPrice;

    @Field("number_of_items")
    private long numberOfItems;

    @Field("total_quantity")
    private long totalQuantity;

    @Field("ordered_on")
    private Date orderedOn;

    @Field("customer_id")
    private String customerId;

    @Field("products")
    private List<ProductDTO> products;
}