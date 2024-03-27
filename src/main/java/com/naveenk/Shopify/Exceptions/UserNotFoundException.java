package com.naveenk.Shopify.Exceptions;

public class UserNotFoundException extends  RuntimeException {
    public UserNotFoundException(String message){
        super(message);
    }
}
