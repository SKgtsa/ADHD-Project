package com.clankalliance.backbeta.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindGraphResponse {

    //业务成功与否
    private boolean success;

    //操作详情
    private String message;

    //平均专注率
    private double concentration;

    //Y轴点坐标
    private int[] graph;

    //用户令牌
    private String token;

    //训练秒数
    private int sec;
}
