package com.example.electronicstore.dtos;

import com.example.electronicstore.entities.CartItem;
import com.example.electronicstore.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private String cartId;
    private double totalPrice;
    private int totalQuantity;
    private UserDto user;
    private List<CartItemDto> cartItems = new ArrayList<>();
}
