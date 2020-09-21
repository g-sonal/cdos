package com.cdos.confirminvite;

public class MeetingResponseApi {
    private long meetingId;
    private String response;

    protected MeetingResponseApi() {}

    public MeetingResponseApi(long meetingId, String response) {
        this.meetingId = meetingId;
        this.response = response;
    }

    public long getMeetingId() {
        return meetingId;
    }

    public String getResponse() {
        return response;
    }
}