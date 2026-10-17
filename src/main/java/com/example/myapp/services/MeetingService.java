package com.example.myapp.services;

import com.example.myapp.ResourceNotFoundException;
import com.example.myapp.dto.MeetingCreateRequest;
import com.example.myapp.dto.MeetingUpdateRequest;
import com.example.myapp.entities.Meeting;
import com.example.myapp.repositories.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserService userService;

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public Optional<Meeting> getMeetingById(Long id) {
        return meetingRepository.findById(id);
    }

    public Meeting createMeeting(MeetingCreateRequest request) {
        String username = userService.getCurrentUsername();
        if (meetingRepository.existsById(request.id())) {
            throw new IllegalArgumentException("כבר קיימת פגישה עם id " + request.id());
        }
        validateTimes(request.startTime(), request.endTime());
        Meeting meeting = new Meeting();
        meeting.setId(request.id());
        meeting.setSubject(request.subject());
        meeting.setStartTime(request.startTime());
        meeting.setEndTime(request.endTime());
        meeting.setLocation(request.location());
        meeting.setDescription(request.description());
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeeting(Long id, MeetingUpdateRequest request) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("פגישה " + id + " לא נמצאה"));
        validateTimes(request.startTime(), request.endTime());
        meeting.setSubject(request.subject());
        meeting.setStartTime(request.startTime());
        meeting.setEndTime(request.endTime());
        meeting.setLocation(request.location());
        meeting.setDescription(request.description());
        return meetingRepository.save(meeting);
    }

    public void deleteMeeting(Long id) {
        meetingRepository.deleteById(id);
    }

    public List<Meeting> getMyMeetings() {
        String username = userService.getCurrentUsername();
        return meetingRepository.findByParticipants_User_Username(username);
    }

    private void validateTimes(Date start, Date end) {
        if (!end.after(start)) {
            throw new IllegalArgumentException("זמן הסיום חייב להיות אחרי זמן ההתחלה");
        }
    }
}
