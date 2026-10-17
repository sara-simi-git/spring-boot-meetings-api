package com.example.myapp.dto;

import com.example.myapp.entities.Participant;

public record ParticipantResponse(Long id, Long meetingId, Long userId, String username, String roleInMeeting) {

    public static ParticipantResponse from(Participant p) {
        return new ParticipantResponse(p.getId(), p.getMeeting().getId(), p.getUser().getId(),
                p.getUser().getUsername(), p.getRoleInMeeting());
    }
}
