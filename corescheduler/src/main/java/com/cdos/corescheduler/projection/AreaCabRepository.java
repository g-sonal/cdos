package com.cdos.corescheduler.projection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AreaCabRepository extends CrudRepository<AreaCab, Integer> {

    @Transactional
    @Modifying
    @Query("UPDATE AreaCab SET freeCabSeats = :freeCabSeats " +
            "WHERE areaCode = :areaCode")
    public void updateCabSeats(@Param("areaCode") int areaCode, @Param("freeCabSeats") String freeCabSeats);
}