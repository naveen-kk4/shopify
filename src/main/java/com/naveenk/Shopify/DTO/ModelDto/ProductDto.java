package com.naveenk.Shopify.DTO.ModelDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {

    private int ordered;
    private int price;
    private int available;
}
