package com.clankalliance.backbeta.utils.StatusManipulateUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.random.RandomGenerator;

@Data
@NoArgsConstructor
public class StatusNode {

    private String userId;

    private String token;

    private String verifyCode;

    private long updateTime;

    private StatusNode next;


    /**
     * 根据用户id自动生成状态
     * 由用户id与状态更新时间拼接加密获得token,作为一个随时间变化的唯一身份标识
     * @param userId
     */
    public StatusNode(String userId){
        this.userId = userId;
        this.updateTime = System.currentTimeMillis();
        this.token = DigestUtils.sha1Hex("" + this.userId + this.updateTime);
        //五位验证码
        String randomNum = "000000" + RandomGenerator.getDefault().nextInt(99999);
        this.verifyCode = randomNum.substring(randomNum.length() - 7, randomNum.length() - 1);
    }
}
