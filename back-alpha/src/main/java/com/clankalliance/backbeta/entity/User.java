package com.clankalliance.backbeta.entity;


import com.clankalliance.backbeta.entity.training.Training;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
    @JsonSerialize(using= ToStringSerializer.class)
    private String wxOpenId;

    @OneToMany
    private Set<CheckInBody> checkInSet;

    @OneToMany
    private List<Training> trainingList;

}
