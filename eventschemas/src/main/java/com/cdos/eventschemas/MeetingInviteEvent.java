package com.cdos.eventschemas;

import java.util.List;

public class MeetingInviteEvent {

    private String eventType;
    
    private long id;
    private String organizerId;
    private List<String> attendeeIds;
    private String subject;
    private int daysPerWeek;
    private int hoursPerDay;

    public MeetingInviteEvent() {}

    public MeetingInviteEvent(
        String eventType,
        long id,
        String organizerId,
        List<String> attendeeIds,
        String subject,
        int daysPerWeek,
        int hoursPerDay) {

	    this.eventType = eventType;
	    this.id = id;
        this.organizerId = organizerId;
        this.attendeeIds = attendeeIds;
        this.subject = subject;
        this.daysPerWeek = daysPerWeek;
        this.hoursPerDay = hoursPerDay;
    }

    public String getEventType() {
        return eventType;
    }

    public long getId() {
        return id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public List<String> getAttendeeIds() {
        return attendeeIds;
    }

    public String getSubject() {
        return subject;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public int getHoursPerDay() {
        return hoursPerDay;
    }
}
