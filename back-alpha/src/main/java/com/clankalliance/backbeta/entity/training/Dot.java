package com.clankalliance.backbeta.entity.training;

import com.clankalliance.backbeta.utils.SnowFlake;
import com.clankalliance.backbeta.utils.TrainingIdGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

//每次训练的一个点数据
@Entity
@Data
@NoArgsConstructor
public class Dot {

    @Id
    @JsonIgnore
    private String id;

    //时间戳
    private Date time;

    //专注值
    private Integer concentrationValue;



    public Dot(String id, Integer concentrationValue, Date time){
        this.id = id + System.currentTimeMillis();
        this.concentrationValue = concentrationValue;
        this.time = time;
    }

}
