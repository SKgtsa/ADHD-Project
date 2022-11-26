package com.clankalliance.backbeta.response;

import com.clankalliance.backbeta.entity.DateData;
import com.clankalliance.backbeta.utils.HalfTrainingData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//主页信息返回体
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MainInfoResponse {

    private boolean success;

    private String token;

    //今天是周几
    private Integer dayOfWeek;

    //上次训练是什么时候 今天/昨天/前天/n天前
    private String lastTrainingTime;

    //签到列表 true代表签到成功
    private boolean[] checkInArray;

    //本周签到情况 满签+金币 或 距满签还有几天 或 漏签
    private String checkInLetter;

    //平均专注度
    private Integer concentrationE;

    //最近的一天训练数据
    private DateData lastDateTraining;


}
