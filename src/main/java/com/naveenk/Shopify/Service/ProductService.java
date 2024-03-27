package com.naveenk.Shopify.Service;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//as we are using values from application.properties file
//  product need not be stored as an entity

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductService {

    @Value("${product.quantity}")
    private int available;

    @Value("${product.ordered}")
    private int ordered;

    @Value("${product.price}")
    private int price;


}