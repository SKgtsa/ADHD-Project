package com.clankalliance.backbeta.entity.arrayTraining;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Training {

    @Id
    private String id;

    //训练编号
    private Integer mark;

    //金币数
    private Integer gold;

    //图像
    @JsonIgnore
    private String graph;

    //训练数据的平均值
    private Integer average;

    //训练数据的时间
    private Integer length;
}
