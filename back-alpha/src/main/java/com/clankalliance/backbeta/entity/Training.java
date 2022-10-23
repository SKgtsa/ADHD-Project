package com.clankalliance.backbeta.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


//每次训练的数据实体
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Training {

    @Id
    private String id;

    //训练开始时间
    private Date startTime;

    //训练结束时间
    private Date endTime;

    //专注率
    private Double concentrationRate;
}
