package com.example.myapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record MeetingUpdateRequest(
        @NotBlank @Size(max = 200) String subject,
        @NotNull Date startTime,
        @NotNull Date endTime,
        @Size(max = 200) String location,
        @Size(max = 1000) String description
) {}
