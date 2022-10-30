package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonPageableRequest {

    private String token;

    //意义可变的一个属性，可视使用情景用来传递token或对象属性，如名称等
    private String identity;

    private int pageNum;

    private int size;

    @Override
    public String toString() {
        return "CommonPageableRequest{" +
                "token='" + token + '\'' +
                ", identity='" + identity + '\'' +
                ", pageNum=" + pageNum +
                ", size=" + size +
                '}';
    }
}
