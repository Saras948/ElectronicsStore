package com.example.electronicstore.exception;

import com.example.electronicstore.dtos.ApiResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponseMessage> resourceNotFoundExceptionHandler(ResourceNotFoundException exception) {
            logger.info("Exception Handler invoked !!");

            ApiResponseMessage message = ApiResponseMessage.builder()
                    .message(exception.getMessage())
                    .success(true)
                    .status(HttpStatus.NOT_FOUND)
                    .build();
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String,Object>>  handleMethodArgumentNotValid(MethodArgumentNotValidException exception){
            logger.info("MetthodArgumentNotValidException Handler invoked !!");

            Map<String,Object> errors = new HashMap<>();
            exception.getBindingResult().getFieldErrors().forEach(error -> {
                errors.put(error.getField(),error.getDefaultMessage());
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Map<String,Object>> handleDataIntegrityViolationException(DataIntegrityViolationException exception){
            logger.info("DataIntegrityViolationException Handler invoked !!");

            Map<String,Object> errors = new HashMap<>();
            errors.put("message",exception.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    @ExceptionHandler(BadApiRequestException.class)
    public ResponseEntity<ApiResponseMessage> badApiRequestFoundExceptionHandler(BadApiRequestException exception) {
        logger.info("BadApiRequestException Handler invoked !!");

        ApiResponseMessage message = ApiResponseMessage.builder()
                .message(exception.getMessage())
                .success(true)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotActiveException.class)
    public ResponseEntity<ApiResponseMessage> productNotActiveExceptionHandler(ProductNotActiveException exception) {
        logger.info("ProductNotActiveException Handler invoked !!");

        ApiResponseMessage message = ApiResponseMessage.builder()
                .message(exception.getMessage())
                .success(true)
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
