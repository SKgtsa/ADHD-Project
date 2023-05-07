package com.clankalliance.backbeta.entity.blog;

import com.clankalliance.backbeta.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private String id;

    private String content;

    private String images;

    @OneToOne
    private User user;

    private Date time;

    private boolean anonymous;

    @OneToOne
    @JsonIgnore
    private Post post;

}
