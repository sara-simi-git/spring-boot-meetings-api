package com.example.myapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParticipantRequest(
        @NotNull Long meetingId,
        @NotNull Long userId,
        @NotBlank @Size(max = 50) String roleInMeeting
) {}
