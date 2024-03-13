package com.example.electronicstore.controllers;

import com.example.electronicstore.dtos.JwtRequest;
import com.example.electronicstore.dtos.JwtResponse;
import com.example.electronicstore.dtos.UserDto;
import com.example.electronicstore.exception.BadApiRequestException;
import com.example.electronicstore.security.JwtHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        String name = principal.getName();
        return new ResponseEntity<>(modelMapper.map(userDetailsService.loadUserByUsername(name),UserDto.class), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> logIn(@RequestBody JwtRequest jwtRequest)
    {
        doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getEmail());
        String token = jwtHelper.generateToken(userDetails);

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        JwtResponse.builder()
                .token(token)
                .userDto(userDto)
                .build();
        return new ResponseEntity<>(JwtResponse.builder()
                .token(token)
                .userDto(userDto)
                .build(), HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        try{
            authenticationManager.authenticate(token);

        } catch (Exception e) {
            throw new BadApiRequestException("Invalid email/password !! ");
        }

    }
}
