package com.cdos.sendinvite.projection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cdos.sendinvite.projection.MeetingInvite;

public interface MeetingInviteRepository extends CrudRepository<MeetingInvite, Long> {

    @Query("SELECT max(id) FROM MeetingInvite")
    Long getMaxId();
}