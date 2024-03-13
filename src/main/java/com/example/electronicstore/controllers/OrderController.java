package com.example.electronicstore.controllers;

import com.example.electronicstore.dtos.ApiResponseMessage;
import com.example.electronicstore.dtos.OrderDto;
import com.example.electronicstore.dtos.ProductDto;
import com.example.electronicstore.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createOrder(@RequestBody OrderDto orderDto, @PathVariable String userId){
        try {
            return new ResponseEntity<OrderDto>(orderService.createOrder(orderDto, userId), HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
            ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                    .message(e.getMessage())
                    .success(false)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            return new ResponseEntity<ApiResponseMessage>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
