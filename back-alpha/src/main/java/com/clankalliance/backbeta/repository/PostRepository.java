package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.blog.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,String> {

    @Query("from Post p where p.id=?1")
    Optional<Post> findById(String id);

    @Query("from Post p where ?1='' or p.heading like %?1%")
    Page<Post> findAllByHeading(String keyWord, Pageable pageable);

}
