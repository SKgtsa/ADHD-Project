package com.clankalliance.backbeta.entity.training;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

//每次训练的一个点数据
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dot {

    @Id
    private long id;

    //时间戳
    private long time;

    //专注值
    private Double concentrationValue;

}
