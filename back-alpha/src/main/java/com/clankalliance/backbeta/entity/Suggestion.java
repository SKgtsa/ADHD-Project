package com.clankalliance.backbeta.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suggestion",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id"),
        })
public class Suggestion {
    @Id
    private long id;

    private String content;

}
