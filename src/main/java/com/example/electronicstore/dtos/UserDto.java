package com.example.electronicstore.dtos;

import com.example.electronicstore.utilities.RegexPattern;
import com.example.electronicstore.validator.ImageNameValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String userId;

    @Size(min = 3, max = 20 , message = "Name must be between 3 and 15 characters")
    private String name;

//    @Email(message = "Please enter a valid email")
    @Pattern(regexp = RegexPattern.EMAIL_PATTERN , message = "Invalid User Email!! ")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Size(min = 4 ,max = 6,message = "Gender must be between 4 and 6 characters")
    private String gender;

    @NotBlank(message = "Write something about Yourself!!")
    private String about;

    @ImageNameValid
    private String imageName;
}
