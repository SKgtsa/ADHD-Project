package com.clankalliance.backbeta.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "wxOpenId"),
        })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    private String wxOpenId;

}
