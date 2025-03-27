package com.student_loan.controller;

import com.student_loan.model.User;
import com.student_loan.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private UserService userService;

    // Constructor
        public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("The user already exists");
        }
        userService.registerUser(user);
        return ResponseEntity.ok("User registered correctly");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String token = userService.loginUser(user.getEmail(), user.getPassword());
        if (token == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
    }
}
