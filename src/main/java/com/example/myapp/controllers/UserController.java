package com.example.myapp.controllers;

import com.example.myapp.dto.UserCreateRequest;
import com.example.myapp.dto.UserResponse;
import com.example.myapp.dto.UserUpdateRequest;
import com.example.myapp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String Hello() {return "hello";}

    @GetMapping("all")
    @PreAuthorize("hasRole('ADMIN')") 
    public List<UserResponse> getUsers() { 
        return userService.getAllUsers().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(#id, authentication.name)") 
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) { 
        return userService.getUserById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) { 
        return UserResponse.from(userService.createUser(request));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) { 
        return UserResponse.from(userService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
