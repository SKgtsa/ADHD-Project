package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonLoginResponse;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.MainInfoResponse;

public interface UserService {

    CommonLoginResponse handleLogin(String code);

    CommonResponse handleFindUserInfo(String token);

    CommonResponse changeNickName(String nickName,String token);

    MainInfoResponse handleMainInfo(String token);

    CommonResponse handleUpdate(String nickName,String avatarURL,String token);

    CommonResponse handleUpdateNickName(String nickName,String token);

    CommonResponse handleUpdateAvatar(String avatarURL,String token);

}
