package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenCheckRequest {

    //前后端token校验请求只需要传token一个参数
    //单独作为一个请求对象

    private String token;

    @Override
    public String toString() {
        return "TokenCheckRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
