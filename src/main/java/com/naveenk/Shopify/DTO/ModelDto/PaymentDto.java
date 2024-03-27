package com.naveenk.Shopify.DTO.ModelDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private int userId;
    private int orderId;
    private int transactionId;
    private String status;
    private String description;

}
