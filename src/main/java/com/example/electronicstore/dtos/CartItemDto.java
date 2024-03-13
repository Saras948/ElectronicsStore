package com.example.electronicstore.dtos;

import com.example.electronicstore.entities.Cart;
import com.example.electronicstore.entities.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDto {
    private Long cartItemId;
    private ProductDto product;
    private int quantity;
    private double totalPrice;
    @JsonIgnore
    private CartDto cartDto;
}
