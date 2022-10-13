package com.clankalliance.backbeta.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class SignatureVerificationUtil {

    /**
     * 验证微信小程序前端与微信接口端的签名
     * @param signatureFront 前端调用微信登录时获得的签名
     * @param sessionKey 后端向微信后台发送请求时得到的session_key
     * @param rawData 用户非敏感信息
     */
    public static boolean verify(String signatureFront,String sessionKey,String rawData){
        return signatureFront.equals(DigestUtils.sha1Hex(rawData+sessionKey));
    }
}
