package com.example.securityexample.controller;

import com.example.securityexample.dto.UserDto;
import com.example.securityexample.dto.UserRequest;
import com.example.securityexample.dto.UserResponse;
import com.example.securityexample.service.concretes.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class UserController {
    private  final UserService userService;
    @PostMapping("/saveUser")
    public ResponseEntity<UserResponse>save(@RequestBody UserDto userDto){
            return ResponseEntity.ok(userService.save(userDto));
    }

    @PostMapping("/auth")
    public ResponseEntity<UserResponse> auth(@RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.auth(userRequest));
    }
}
