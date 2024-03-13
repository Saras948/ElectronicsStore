package com.example.electronicstore.controllers;

import com.example.electronicstore.dtos.*;
import com.example.electronicstore.services.FileService;
import com.example.electronicstore.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    //create user
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        UserDto newUserDto = userService.saveUser(userDto);
        return new ResponseEntity<>(newUserDto, HttpStatus.CREATED);
    }

    //update user
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @PathVariable("userId") String userId, @RequestBody UserDto userDto){
        UserDto updatedUserDto = userService.updateUser(userDto, userId);
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }


    //delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                                .message("User deleted Successfully!!")
                                .success(true)
                                .status(HttpStatus.OK)
                                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    //get all users
    @GetMapping
    public PageableResponse<UserDto> getAllUsers(
            @RequestBody PageDto pageDto) {
        PageableResponse<UserDto> userDtos = userService.getAllUsers(pageDto);
        return userDtos;
    }


    //get user by id
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId){
        UserDto userDto = userService.getUserById(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }



    //get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
        UserDto userDto = userService.getUserByEmail(email);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


    //search user
    @GetMapping("/search/{keyword}")
     public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keyword) {
        List<UserDto> userDtos = userService.searchUser(keyword);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    //upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("imageFile") MultipartFile file,
                                                         @PathVariable String userId) throws IOException {

        String imageName = fileService.uploadFile(file, imageUploadPath);

        UserDto user =  userService.getUserById(userId);
        user.setImageName(imageName);
        UserDto userDto = userService.updateUser(user, userId);

        ImageResponse response = ImageResponse.builder()
                .imageName(imageName)
                .message("Image uploaded successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //get user image
    @GetMapping("/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        UserDto user = userService.getUserById(userId);
        logger.info("User image name : {}",user.getImageName());
        InputStream resource = fileService.getResource(user.getImageName(),imageUploadPath);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        StreamUtils.copy(resource, response.getOutputStream());
    }



}
