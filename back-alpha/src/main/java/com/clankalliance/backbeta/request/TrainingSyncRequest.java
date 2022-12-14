package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//训练数据同步保存请求
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSyncRequest {

    private String token;

    private String rawData;

}
