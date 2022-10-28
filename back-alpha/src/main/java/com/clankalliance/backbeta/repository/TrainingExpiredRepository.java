package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.arrayTraining.TrainingExpired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingExpiredRepository extends JpaRepository<TrainingExpired,Integer> {

    @Query("from TrainingExpired te where te.id=?1")
    Optional<TrainingExpired> findTrainingById(String id);

}
