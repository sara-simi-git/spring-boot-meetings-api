package com.example.myapp.services;

import com.example.myapp.ResourceNotFoundException;
import com.example.myapp.dto.UserCreateRequest;
import com.example.myapp.dto.UserUpdateRequest;
import com.example.myapp.entities.User;
import com.example.myapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication.getName();
    }

    public User getCurrentUser() {
        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(UserCreateRequest request) {
        if (userRepository.existsById(request.id())) {
            throw new IllegalArgumentException("כבר קיים משתמש עם id " + request.id());
        }
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("שם המשתמש '" + request.username() + "' כבר תפוס");
        }
        User user = new User(request.id(), request.username(), passwordEncoder.encode(request.password()),
                request.email(), request.phone(), request.admin());
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("משתמש " + id + " לא נמצא"));
        if (userRepository.findByUsername(request.username()).filter(u -> u.getId() != id).isPresent()) {
            throw new IllegalArgumentException("שם המשתמש '" + request.username() + "' כבר תפוס");
        }
        user.setUsername(request.username());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setAdmin(request.admin());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean isOwner(Long id, String username) {
        return userRepository.findById(id)
                .map(u -> u.getUsername().equals(username))
                .orElse(false);
    }
}
