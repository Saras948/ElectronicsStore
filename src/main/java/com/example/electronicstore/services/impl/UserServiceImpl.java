package com.example.electronicstore.services.impl;

import com.example.electronicstore.dtos.PageDto;
import com.example.electronicstore.dtos.PageableResponse;
import com.example.electronicstore.dtos.UserDto;
import com.example.electronicstore.entities.User;
import com.example.electronicstore.exception.ResourceNotFoundException;
import com.example.electronicstore.repositories.UserRepository;
import com.example.electronicstore.services.UserService;
import com.example.electronicstore.utilities.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    @Autowired
    private PasswordEncoder passwordEndcoder;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDto saveUser(UserDto userDto) {

        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        userDto.setPassword(passwordEndcoder.encode(userDto.getPassword()));
        User user = dtoToEntity(userDto);
        user.setCreatedDate(Helper.getCurrentDate());
        user.setStatus("Active");
        User savedUser = userRepository.save(user);

        UserDto newUserDto = entityToDto(savedUser);
        return newUserDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        user.setName(userDto.getName());
        //user.setEmail(userDto.getEmail());
        user.setPassword(passwordEndcoder.encode(userDto.getPassword()));
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setImageName(userDto.getImageName());
        user.setUpdatedDate(Helper.getCurrentDate());
        User updateUser = userRepository.save(user);

        UserDto updatedUserDto = entityToDto(updateUser);
        return updatedUserDto;
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));

        String imageName = user.getImageName();
        logger.info("Image name: " + imageName);
        try {
            String fullPath = imageUploadPath + "/" + imageName;

            Path path = Path.of(fullPath);
            Files.delete(path);
        }
        catch(NoSuchFileException ex)
        {
            logger.error("Image not found with name: " + imageName);
        }
        catch(IOException ex)
        {
            logger.error("Error occurred while deleting image: " + imageName);
            ex.printStackTrace();
        }

        //delete user
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(PageDto pageDto) {

        Sort sort = (pageDto.getSortDir().equalsIgnoreCase("Desc")) ?
                (Sort.by(pageDto.getSortBy()).descending()): (Sort.by(pageDto.getSortBy()).ascending());


        Page<User> page = userRepository.findAll(PageRequest.of(pageDto.getPageNumber(), pageDto.getPageSize(), sort));

        //convert user page to  userDto pageResponse
        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);

        return response;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        return entityToDto(user);

    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with given email"));
        return entityToDto(user);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        //convert list of users to list of userDto
        List<UserDto> dtoList = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        return dtoList;
    }

    private UserDto entityToDto(User user) {
//        UserDto userDto = UserDto.builder()
//                .userId(user.getId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .password(user.getPassword())
//                .about(user.getAbout())
//                .gender(user.getGender())
//                .imageName(user.getImageName())
//                .build();
//        return userDto;

        return modelMapper.map(user, UserDto.class);
    }

    private User dtoToEntity(UserDto userDto) {
//        User user = User.builder()
//                .id(userDto.getUserId())
//                .name(userDto.getName())
//                .email(userDto.getEmail())
//                .password(userDto.getPassword())
//                .about(userDto.getAbout())
//                .gender(userDto.getGender())
//                .imageName(userDto.getImageName())
//                .build();
//        return user;
        return modelMapper.map(userDto, User.class);
    }
}
