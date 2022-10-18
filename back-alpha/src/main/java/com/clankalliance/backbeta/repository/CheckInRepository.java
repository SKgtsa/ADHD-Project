package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.CheckInBody;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckInBody, Integer> {
}
