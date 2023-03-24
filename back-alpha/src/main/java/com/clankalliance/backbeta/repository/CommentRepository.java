package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.blog.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,String> {

    @Query("from Comment c where c.id=?1")
    Optional<Comment> findById(String id);

}
