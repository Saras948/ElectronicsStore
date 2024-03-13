package com.example.electronicstore.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddItemToCartRequest {
    private String productId;
    private int quantity;
}
