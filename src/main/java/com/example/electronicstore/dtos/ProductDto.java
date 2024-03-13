package com.example.electronicstore.dtos;


import com.example.electronicstore.entities.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDto {
    private String productId;
    @NotBlank(message = "Product title cannot be blank!!")
    @Size(min = 3, max = 50, message = "Product title must be between 3 and 50 characters!!")
    private String title;
    @NotBlank(message = "Product description is required!!")
    private String description;
    private String liveStatus;
    private double productPrice;
    private String productImageName;
    @NotBlank(message = "Product quantity is required!!")
    @Size(min = 1, message = "Product quantity must be greater than 0!!")
    private String quantity;
    private String stockStatus;
    private CategoryDto categoryDto;


}
