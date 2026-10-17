package com.example.myapp.controllers;

import com.example.myapp.entities.User;
import com.example.myapp.jwt.JwtUtil;
import com.example.myapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository; 
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public record LoginRequest(String username, String password) {}

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        if (request.username() == null || request.password() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }

        Optional<User> userOpt = userRepository.findByUsername(request.username());


        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }

        String role = userOpt.get().isAdmin() ? "ROLE_ADMIN" : "ROLE_PERSON";
        String token = jwtUtil.generateToken(userOpt.get().getUsername(), role);
        return ResponseEntity.ok(token);
    }
}
