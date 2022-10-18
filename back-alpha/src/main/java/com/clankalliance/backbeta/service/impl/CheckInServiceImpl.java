package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.CheckInBody;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.repository.CheckInRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CheckInService;
import com.clankalliance.backbeta.utils.SnowFlake;
import com.clankalliance.backbeta.utils.TokenUtil;

import javax.annotation.Resource;
import java.util.Date;

public class CheckInServiceImpl implements CheckInService {


    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private CheckInRepository checkInRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    public CommonResponse handleSave(String token) {
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(response.getSuccess()){
            User user = userRepository.findUserByOpenId(response.getMessage()).get();
            CheckInBody checkIn = new CheckInBody(snowFlake.nextId(), new Date(), user);
        }
        return response;
    }
}
