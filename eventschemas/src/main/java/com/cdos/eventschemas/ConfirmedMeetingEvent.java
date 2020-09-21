package com.cdos.eventschemas;

import java.util.List;

public class ConfirmedMeetingEvent {

    private long meetingId;
    private String subject;
    private List<String> attendeeIds;
    private int daysPerWeek;
    private int hoursPerDay;

    protected ConfirmedMeetingEvent() {}

    public ConfirmedMeetingEvent(long meetingId, String subject, List<String> attendeeIds, int daysPerWeek, int hoursPerDay) {
        this.meetingId = meetingId;
        this.subject = subject;
        this.attendeeIds = attendeeIds;
        this.daysPerWeek = daysPerWeek;
        this.hoursPerDay = hoursPerDay;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public String getSubject() {
        return subject;
    }
    
    public List<String> getAttendeeIds() {
        return attendeeIds;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public int getHoursPerDay() {
        return hoursPerDay;
    }
}
