package com.cdos.confirminvite.projection;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MeetingInvite {

    @Id
    private Long id;
    private String organizerId;
    private int inviteesCount;
    private int responseCount;
    private String subject;
    private int daysPerWeek;
    private int hoursPerDay;

    protected MeetingInvite() {}

    public MeetingInvite(Long id, String organizerId, int inviteesCount, String subject, int daysPerWeek, int hoursPerDay) {
        this.id = id;
        this.organizerId = organizerId;
        this.inviteesCount = inviteesCount;
        this.subject = subject;
        this.daysPerWeek = daysPerWeek;
        this.hoursPerDay = hoursPerDay;
    }

    public Long getId() {
        return id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public int getInviteesCount() {
        return inviteesCount;
    }
    
    public int getResponseCount() {
        return responseCount;
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