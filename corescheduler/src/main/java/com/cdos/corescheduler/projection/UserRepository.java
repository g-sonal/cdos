package com.cdos.corescheduler.projection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends CrudRepository<User, String> {

    @Transactional
    @Modifying
    @Query("UPDATE User SET slots = :slots " +
            "WHERE userId = :userId")
    void updateSlot(@Param("userId") String userId, @Param("slots") String slots);
}