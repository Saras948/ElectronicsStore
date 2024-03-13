package com.example.electronicstore.exception;

public class ProductNotActiveException extends RuntimeException{

    public ProductNotActiveException(String message){
        super(message);
    }
}
