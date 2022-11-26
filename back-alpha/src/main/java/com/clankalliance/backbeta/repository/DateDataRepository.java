package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.DateData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DateDataRepository extends JpaRepository<DateData,String> {

    @Query("from DateData d where d.id=?1")
    Optional<DateData> findDateDataById(String id);

}
