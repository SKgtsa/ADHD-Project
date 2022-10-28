package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.arrayTraining.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training,Integer> {

    @Query("from Training t where t.id=?1")
    Optional<Training> findTrainingById(String id);

}
