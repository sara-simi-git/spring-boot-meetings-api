package com.example.myapp.dto;

import com.example.myapp.entities.Meeting;

import java.util.Date;

public record MeetingResponse(Long id, String subject, Date startTime, Date endTime, String location, String description) {

    public static MeetingResponse from(Meeting m) {
        return new MeetingResponse(m.getId(), m.getSubject(), m.getStartTime(), m.getEndTime(),
                m.getLocation(), m.getDescription());
    }
}
