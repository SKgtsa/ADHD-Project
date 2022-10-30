package com.clankalliance.backbeta.request;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//用户信息保存请求

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveRequest {
    private long id;

    private String loginName;//LoginName

    private String name;

    private String password;


    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
