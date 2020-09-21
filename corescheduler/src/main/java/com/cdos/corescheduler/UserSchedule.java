package com.cdos.corescheduler;

import java.time.LocalDateTime;

public interface UserSchedule {
    public String getUserId();
    public String getMeetingSubject();
    public LocalDateTime getDateTime();
}