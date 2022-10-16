package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository  extends JpaRepository<Suggestion,Integer> {

}
