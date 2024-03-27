package com.naveenk.Shopify.Exceptions;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException(String invalidQuantity) {
        super(invalidQuantity);
    }
}
