package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestionRepository  extends JpaRepository<Suggestion,Integer> {

}
