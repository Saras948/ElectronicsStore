package com.example.electronicstore.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private String categoryId;
    @NotBlank
    @Size(min=3,message = "Category title must have at least 3 characters ")
    private String title;

    @NotBlank(message = "Category description is required !!")
    private String description;

    private String coverImageName;
}
