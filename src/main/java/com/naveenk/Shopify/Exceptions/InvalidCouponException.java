package com.naveenk.Shopify.Exceptions;

public class InvalidCouponException extends RuntimeException {

    public InvalidCouponException(String msg){
        super(msg);
    }
}
