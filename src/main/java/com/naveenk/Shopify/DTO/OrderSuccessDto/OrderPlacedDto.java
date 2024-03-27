package com.naveenk.Shopify.DTO.OrderSuccessDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedDto {

    private int orderId;
    private int userId;
    private int quantity;
    private int amount;
    private String coupon;
}
