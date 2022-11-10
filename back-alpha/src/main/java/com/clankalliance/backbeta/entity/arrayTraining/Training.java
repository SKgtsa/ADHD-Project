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

    //训练年份
    private Integer year;

    //月
    private Integer month;

    //日
    private Integer day;

    //训练时间位于一年的第几个周
    private Integer weekOfTheYear;

    //训练时间位于周几
    private Integer dayOfTheWeek;

    //金币数
    private Integer gold;

    //图像
    @JsonIgnore
    private String graph;

    //训练数据的平均值
    private Integer average;

    //训练数据的长度
    private Integer length;
}
