package com.clankalliance.backbeta.repository;

import com.clankalliance.backbeta.entity.blog.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,String> {

    @Query("from Post p where p.id=?1")
    Optional<Post> findById(String id);

    @Query(value = "SELECT * FROM post WHERE ?1='' OR heading like %?1% ORDER BY time DESC", nativeQuery = true)
    List<Post> findByHeadingContaining(String heading);

}
