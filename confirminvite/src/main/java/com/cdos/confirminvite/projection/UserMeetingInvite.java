package com.cdos.confirminvite.projection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserMeetingInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String userId;
    private long meetingId;
    private String response;

    protected UserMeetingInvite() {}

    public UserMeetingInvite(String userId, long meetingId) {
        this.userId = userId;
        this.meetingId = meetingId;
    }

    public String getUserId() {
        return userId;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public String getResponse() {
        return response;
    }

    /*public void setResponse(String response) {
        this.response = response;
    }*/
}