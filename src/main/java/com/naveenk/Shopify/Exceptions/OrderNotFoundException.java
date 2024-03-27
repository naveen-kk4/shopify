package com.naveenk.Shopify.Exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String invalidOrderId) {
        super(invalidOrderId);
    }
}
