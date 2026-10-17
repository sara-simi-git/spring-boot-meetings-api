package com.example.myapp.services;

import com.example.myapp.ResourceNotFoundException;
import com.example.myapp.dto.ParticipantRequest;
import com.example.myapp.entities.Meeting;
import com.example.myapp.entities.Participant;
import com.example.myapp.entities.User;
import com.example.myapp.repositories.MeetingRepository;
import com.example.myapp.repositories.ParticipantRepository;
import com.example.myapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository _participantRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    public List<Participant> getAllParticipants() {
        return _participantRepository.findAll();
    }

    public Optional<Participant> getParticipantById(Long id) {
        return _participantRepository.findById(id);
    }

    public Participant createParticipant(ParticipantRequest request) {
        Participant participant = new Participant();
        fillParticipant(participant, request); 
        return _participantRepository.save(participant);
    }

    public Participant updateParticipant(Long id, ParticipantRequest request) {
        Participant participant = _participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("משתתף " + id + " לא נמצא"));
        fillParticipant(participant, request);
        return _participantRepository.save(participant);
    }

    public void deleteParticipant(Long id) {
        _participantRepository.deleteById(id);
    }

    private void fillParticipant(Participant participant, ParticipantRequest request) {
        Meeting meeting = meetingRepository.findById(request.meetingId())
                .orElseThrow(() -> new ResourceNotFoundException("פגישה " + request.meetingId() + " לא נמצאה"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("משתמש " + request.userId() + " לא נמצא"));
        participant.setMeeting(meeting);
        participant.setUser(user);
        participant.setRoleInMeeting(request.roleInMeeting());
    }
}
