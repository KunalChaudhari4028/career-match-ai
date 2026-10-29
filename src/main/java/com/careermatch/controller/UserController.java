package com.careermatch.controller;

import com.careermatch.dto.UserRegistrationDto;
import com.careermatch.entity.User;
import com.careermatch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(
            @Valid @RequestBody UserRegistrationDto dto) {

        User user = userService.registerUser(dto);

        Map<String, Object> response = Map.of(
                "message", "User registered successfully",
                "userId", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
