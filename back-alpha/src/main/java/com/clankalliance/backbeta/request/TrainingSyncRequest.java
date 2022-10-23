package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//训练数据同步保存请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSyncRequest {

    private String token;

    private Date startTime;

    private Date endTime;

    private Double concentrationRate;

}
