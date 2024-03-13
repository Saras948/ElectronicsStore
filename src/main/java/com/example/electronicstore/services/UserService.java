package com.example.electronicstore.services;

import com.example.electronicstore.dtos.PageDto;
import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.dtos.UserDto;
import java.util.List;

public interface UserService {

    //create
    UserDto saveUser(UserDto userDto);

    //update
    UserDto updateUser(UserDto userDto, String userId);

    //delete
    void deleteUser(String userId);

    //get all users
    PageableResponse<UserDto> getAllUsers(PageDto pageDto);

    //get single user by id
    UserDto getUserById(String userId);

    //get single user by email
    UserDto getUserByEmail(String email);

    //search user
    List<UserDto> searchUser(String keyword);

    // other user related features
}
