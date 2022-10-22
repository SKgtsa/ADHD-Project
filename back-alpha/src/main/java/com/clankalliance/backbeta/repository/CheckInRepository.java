package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.CheckInBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckInBody, Integer> {

    @Query(value = "from CheckInBody c where c.user.wxOpenId=?1")
    List<CheckInBody> findCheckInBodyByUserId(String userId);

    @Query(value = "from CheckInBody c where c.user.wxOpenId=?1 and c.date=?2")
    Optional<CheckInBody> findCheckInBodyByUserIdAndDate(String userId, LocalDate date);

}
