package com.example.myapp.controllers;

import com.example.myapp.dto.ParticipantRequest;
import com.example.myapp.dto.ParticipantResponse;
import com.example.myapp.services.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService _participantService;

    @GetMapping
    public List<ParticipantResponse> getParticipants() { 
        return _participantService.getAllParticipants().stream().map(ParticipantResponse::from).toList();
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<ParticipantResponse> getParticipantById(@PathVariable Long id) { 
        return _participantService.getParticipantById(id)
                .map(ParticipantResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public ParticipantResponse createParticipant(@Valid @RequestBody ParticipantRequest request) { 
        return ParticipantResponse.from(_participantService.createParticipant(request));
    }

    @PutMapping("/participants/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ParticipantResponse updateParticipant(@PathVariable Long id, @Valid @RequestBody ParticipantRequest request) { 
        return ParticipantResponse.from(_participantService.updateParticipant(id, request));
    }

    @DeleteMapping("/participants/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        _participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }
}
