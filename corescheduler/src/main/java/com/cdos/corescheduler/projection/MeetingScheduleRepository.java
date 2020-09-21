package com.cdos.corescheduler.projection;

import com.cdos.corescheduler.UserSchedule;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MeetingScheduleRepository extends CrudRepository<MeetingSchedule, Long> {

    @Query("SELECT u.userId as userId, mi.subject as meetingSubject, m.dateTime as dateTime " +
            "FROM MeetingSchedule m, UserMeetingInvite u, MeetingInvite mi " +
            "WHERE m.meetingId = :meetingId AND u.meetingId = :meetingId AND mi.id = :meetingId")
    public Iterable<UserSchedule> getScheduleForMeetingId(@Param("meetingId") String meetingId);

    @Query("SELECT u.userId as userId, mi.subject as meetingSubject, m.dateTime as dateTime " +
            "FROM MeetingSchedule m, UserMeetingInvite u, MeetingInvite mi " +
            "WHERE u.userId = :userId AND m.meetingId = u.meetingId AND mi.id = u.meetingId")
    public Iterable<UserSchedule> getScheduleForUserId(@Param("userId") String userId);
}