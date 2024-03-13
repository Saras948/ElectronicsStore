package com.example.electronicstore.exception;

public class BadApiRequestException extends RuntimeException {

    public BadApiRequestException() {
        super("Bad API Request Found");
    }
    public BadApiRequestException(String message) {
        super(message);
    }
}
