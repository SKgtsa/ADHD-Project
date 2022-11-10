package com.clankalliance.backbeta.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HalfTrainingData {

    private Integer year;

    private Integer month;

    private Integer day;

    private Integer gold;

    //平均专注度
    private Integer concentrationE;

    //本日训练时长
    private long time;

    //该日数据对应数据库中list的开始位置
    private Integer startIndex;

    //训练总个数
    private Integer trainingNum;

    //训练时间的方差
    private Double timeVariance;

}
