package com.clankalliance.backbeta.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "checkIn")
public class CheckInBody {

    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private long id;

    private Date date;

    @ManyToOne
    private User user;


}
