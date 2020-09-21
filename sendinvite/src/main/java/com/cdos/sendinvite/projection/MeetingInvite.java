package com.cdos.sendinvite.projection;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MeetingInvite {

    @Id
    private Long meetingId;
    private String organizerId;
    private String subject;

    protected MeetingInvite() {}

    public MeetingInvite(Long meetingId, String organizerId, String subject) {
        this.meetingId = meetingId;
        this.organizerId = organizerId;
        this.subject = subject;
    }

    public Long getId() {
        return meetingId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getSubject() {
        return subject;
    }
}
