package com.example.electronicstore.controllers;

import com.example.electronicstore.dtos.AddItemToCartRequest;
import com.example.electronicstore.dtos.ApiResponseMessage;
import com.example.electronicstore.dtos.CartDto;
import com.example.electronicstore.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(@PathVariable String userId,
                                                     @RequestBody AddItemToCartRequest addItemToCartRequest){
        CartDto cartDto = cartService.addItemToCart(userId,addItemToCartRequest);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/item/{cartItemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable String userId,
                                                                 @PathVariable int cartItemId){
        cartService.removeItemFromCart(userId,cartItemId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Item removed from cart successfully!!")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable String userId){
        cartService.clearCart(userId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Cart cleared successfully!!")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCartByUser(@PathVariable String userId){
        CartDto cartDto = cartService.getCartByUser(userId);
        return new ResponseEntity<>(cartDto,HttpStatus.OK);
    }
}
