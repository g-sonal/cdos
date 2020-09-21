package com.cdos.confirminvite.projection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MeetingInviteRepository extends CrudRepository<MeetingInvite, Long> {

    @Query("SELECT inviteesCount = responseCount FROM MeetingInvite " + 
            "WHERE id = :id")
    boolean haveAllInviteesResponded(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE MeetingInvite SET responseCount = responseCount + 1 " +
            "WHERE id = :id")
    void incrementResponseCount(@Param(value = "id") Long id);
}