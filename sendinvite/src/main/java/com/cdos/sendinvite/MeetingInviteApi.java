package com.cdos.sendinvite;

import java.util.List;

public class MeetingInviteApi {

    private String organizerId;
    private List<String> attendeeIds;
    private String subject;
    private int daysPerWeek;
    private int hoursPerDay;

    protected MeetingInviteApi() {}

    public MeetingInviteApi(String organizerId, List<String> attendeeIds, String subject, int daysPerWeek, int hoursPerDay) {
        this.organizerId = organizerId;
        this.attendeeIds = attendeeIds;
        this.subject = subject;
        this.daysPerWeek = daysPerWeek;
        this.hoursPerDay = hoursPerDay;
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
