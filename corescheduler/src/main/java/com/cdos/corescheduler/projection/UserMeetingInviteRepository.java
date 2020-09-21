package com.cdos.corescheduler.projection;

import org.springframework.data.repository.CrudRepository;

public interface UserMeetingInviteRepository extends CrudRepository<UserMeetingInvite, Long> {

    /*@Query("SELECT m.organizerId as organizerId, m.subject as subject, m.daysPerWeek as daysPerWeek, m.hoursPerDay as hoursPerDay " +
            "FROM MeetingInvite m, UserMeetingInvite u " +
            "WHERE u.userId = :userId")
    Iterable<Schedule> findForUserId(@Param("userId") String userId);*/
}