package com.cdos.corescheduler.projection;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class MeetingInvite {

    @Id
    private Long id;
    private String subject;

    protected MeetingInvite() {}

    public MeetingInvite(Long id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }
}