package com.cdos.confirminvite.projection;

import java.util.List;
import java.util.Optional;

import com.cdos.confirminvite.MeetingInviteSummary;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserMeetingInviteRepository extends CrudRepository<UserMeetingInvite, Long> {

    Optional<UserMeetingInvite> findByUserIdAndMeetingId(String userId, long meetingId);

    @Query("SELECT m.id as meetingId, m.organizerId as organizerId, m.subject as subject, m.daysPerWeek as daysPerWeek, m.hoursPerDay as hoursPerDay " +
            "FROM MeetingInvite m, UserMeetingInvite u " +
            "WHERE u.userId = :userId AND m.id = u.meetingId")
    Iterable<MeetingInviteSummary> findForUserId(@Param("userId") String userId);

    @Query("SELECT userId FROM UserMeetingInvite " +
            "WHERE meetingId = :meetingId AND response = :response")
    List<String> findAttendees(@Param("meetingId") long meetingId, @Param("response") String response);

    @Transactional
    @Modifying
    @Query("UPDATE UserMeetingInvite " +
            "SET response = :response " +
            "WHERE userId = :userId AND meetingId = :meetingId")
    void updateResponse(@Param("userId") String userId, @Param("meetingId") long meetingId, @Param("response") String response);
}