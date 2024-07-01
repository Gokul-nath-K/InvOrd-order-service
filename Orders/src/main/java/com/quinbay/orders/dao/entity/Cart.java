package com.quinbay.orders.dao.entity;

import com.quinbay.orders.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "carts")
public class Cart {

    @Id
    private String id;

    @Field("code")
    private String code;

    @Field("products")
    private List<ProductDTO> products;
}