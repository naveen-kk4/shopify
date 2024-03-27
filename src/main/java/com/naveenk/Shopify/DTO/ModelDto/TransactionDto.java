package com.naveenk.Shopify.DTO.ModelDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private int orderId;
    private int amount;
    private Date date;
    private String coupon;
    private int transactionId;
    private String status;
}
