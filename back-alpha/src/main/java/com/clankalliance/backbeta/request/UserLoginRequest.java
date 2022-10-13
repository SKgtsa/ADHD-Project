package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//用户登录请求对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    private String code;

    private String signature;

    private String rawData;
}
