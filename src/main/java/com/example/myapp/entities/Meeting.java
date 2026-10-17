package com.example.myapp.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; 
import lombok.Data;
import lombok.EqualsAndHashCode; 
import lombok.ToString; 

import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    private Long id;

    private String subject;

    private Date startTime;

    private Date endTime;

    private String location;
    private String description;


    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Participant> participants;
    public Meeting() {}
}