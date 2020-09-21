package com.cdos.confirminvite;

public interface MeetingInviteSummary {
    public long getMeetingId();
    public String getOrganizerId();
    public String getSubject();
    public int getDaysPerWeek();
    public int getHoursPerDay();
}