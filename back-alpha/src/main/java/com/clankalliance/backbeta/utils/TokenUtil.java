package com.clankalliance.backbeta.utils;

import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.utils.StatusManipulateUtilsWithRedis.ManipulateUtilRedis;
import com.clankalliance.backbeta.utils.StatusManipulateUtilsWithRedis.StatusNodeWithRedis;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class TokenUtil {

    @Resource
    private ManipulateUtilRedis manipulateUtilRedis;

    //验证token是否有效
    public CommonResponse tokenCheck(String token){
        CommonResponse response = new CommonResponse();
        StatusNodeWithRedis status =  manipulateUtilRedis.findStatusByToken(token);
        if(status != null){
            //正常查找到状态
            response.setSuccess(true);
            response.setToken(manipulateUtilRedis.updateStatus(status.getUserId()));
            //response中message设置为用户id,传回前台前需要处理
            //因为大部分token查询后需要返回个id使用,所以这样设计
            response.setMessage(status.getUserId());
        }else{
            response.setSuccess(false);
            response.setMessage("登录失效");
        }
        return response;
    }

    public CommonResponse updateDot(String id, int dot){
        CommonResponse response = new CommonResponse();
        StatusNodeWithRedis status =  manipulateUtilRedis.findStatusById(id);
        if(status != null){
            //正常查找到状态
            //没有删除旧状态
            manipulateUtilRedis.updateStatus(id,dot);
            response.setSuccess(true);
            response.setMessage("更新成功");
        }else{
            response.setSuccess(false);
            response.setMessage("没有找到用户");
        }
        return response;
    }

    public CommonResponse getDot(String token){
        CommonResponse response = new CommonResponse();
        StatusNodeWithRedis status =  manipulateUtilRedis.findStatusByToken(token);
        if(status != null){
            //正常查找到状态
            //没有删除旧状态
            response.setSuccess(true);
            response.setToken(manipulateUtilRedis.updateStatus(status.getUserId()));
            //response中message设置为用户id,传回前台前需要处理
            //因为大部分token查询后需要返回个id使用,所以这样设计
            response.setMessage(status.getUserId());
            response.setContent(status.getCurrentDot());
        }else{
            response.setSuccess(false);
            response.setMessage("登录失效");
        }
        return response;
    }

}
