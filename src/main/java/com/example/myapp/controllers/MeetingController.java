package com.example.myapp.controllers;

import com.example.myapp.dto.MeetingCreateRequest;
import com.example.myapp.dto.MeetingResponse;
import com.example.myapp.dto.MeetingUpdateRequest;
import com.example.myapp.services.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public List<MeetingResponse> getMeetings() { 
        return meetingService.getAllMeetings().stream().map(MeetingResponse::from).toList();
    }

    @GetMapping("/meetings/{id}")
    public ResponseEntity<MeetingResponse> getMeetingById(@PathVariable Long id) { 
        return meetingService.getMeetingById(id)
                .map(MeetingResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public MeetingResponse createMeeting(@Valid @RequestBody MeetingCreateRequest request) { 
        return MeetingResponse.from(meetingService.createMeeting(request));
    }

    @PutMapping("/meetings/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MeetingResponse updateMeeting(@PathVariable Long id, @Valid @RequestBody MeetingUpdateRequest request) {
        return MeetingResponse.from(meetingService.updateMeeting(id, request));
    }

    @DeleteMapping("/meetings/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public List<MeetingResponse> getMyMeetings() {
        return meetingService.getMyMeetings().stream().map(MeetingResponse::from).toList();
    }
}
