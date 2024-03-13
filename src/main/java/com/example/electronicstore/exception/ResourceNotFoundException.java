package com.example.electronicstore.exception;

import lombok.Builder;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Builder
public class ResourceNotFoundException  extends RuntimeException{

    public ResourceNotFoundException() {
        super("Resource Not Found!!");
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
