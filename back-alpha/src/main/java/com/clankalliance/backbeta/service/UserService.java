package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonLoginResponse;
import com.clankalliance.backbeta.response.CommonResponse;

public interface UserService {

    CommonLoginResponse handleLogin(String code,String signature,String rawData);

    CommonResponse handleFindUserInfo(String token);

}
