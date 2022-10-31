package com.clankalliance.backbeta.utils;

import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.utils.StatusManipulateUtils.ManipulateUtil;
import com.clankalliance.backbeta.utils.StatusManipulateUtils.StatusNode;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class TokenUtil {
    //验证token是否有效
    public CommonResponse tokenCheck(String token){
        CommonResponse response = new CommonResponse();
        StatusNode status =  ManipulateUtil.findStatusByToken(token);
        if(status.getToken() != null){
            //正常查找到状态
            //没有删除旧状态
            ManipulateUtil.appendStatus(status.getUserId());
            response.setSuccess(true);
            response.setToken(ManipulateUtil.endNode.getToken());
            //response中message设置为用户id,传回前台前需要处理
            //因为大部分token查询后需要返回个id使用,所以这样设计
            response.setMessage(ManipulateUtil.endNode.getUserId());
        }else{
            response.setSuccess(false);
            response.setMessage("登录失效");
        }
        return response;
    }

}
