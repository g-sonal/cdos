package com.cdos.corescheduler.projection;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MeetingSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long meetingId;
    private LocalDateTime dateTime;

    protected MeetingSchedule() {}

    public MeetingSchedule(long meetingId, LocalDateTime dateTime) {
        this.meetingId = meetingId;
        this.dateTime = dateTime;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}