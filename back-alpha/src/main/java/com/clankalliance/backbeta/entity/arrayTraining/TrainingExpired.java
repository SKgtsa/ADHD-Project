package com.clankalliance.backbeta.entity.arrayTraining;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TrainingExpired {

    //过期数据
    //不再存储日期，但小程序仍可查询

    @Id
    private String id;

    private Integer mark;

    //金币数
    private Integer gold;

    //图像
    @JsonIgnore
    private String graph;

}
