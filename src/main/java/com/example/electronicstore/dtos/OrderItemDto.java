package com.example.electronicstore.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private int orderItemId;
    @Size(min = 1, message = "Quantity must be at least 1")
    private int quantity;
    private int totalPrice;
    private ProductDto productDto;
    @JsonIgnore
    private OrderDto orderDto;
}
