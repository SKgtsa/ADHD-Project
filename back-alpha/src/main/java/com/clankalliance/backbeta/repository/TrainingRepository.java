package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrainingRepository extends JpaRepository<Training,Integer> {



}
