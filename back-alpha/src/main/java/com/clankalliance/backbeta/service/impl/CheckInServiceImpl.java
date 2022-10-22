package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.CheckInBody;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.repository.CheckInRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CheckInService;
import com.clankalliance.backbeta.utils.SnowFlake;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
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
        System.out.println(response);
        String userId = response.getMessage();
        if(response.getSuccess()){
            LocalDate localDate = LocalDate.now();
            Optional<CheckInBody> oldCheckIn = checkInRepository.findCheckInBodyByUserIdAndDate(userId,localDate);

            List<CheckInBody> historyCheckIn = checkInRepository.findCheckInBodyByUserId(userId);

            response.setMessage("测试文本，可放多个可选文本随机发放");
            if(oldCheckIn.isPresent()){
                response.setContent(historyCheckIn.size());
                response.setSuccess(false);
            }else{
                response.setContent(historyCheckIn.size() + 1);
                Optional<User> uop = userRepository.findUserByOpenId(userId);
                User user = uop.get();
                CheckInBody checkIn = new CheckInBody(snowFlake.nextId(), localDate, user);
                checkInRepository.save(checkIn);
            }
        }else{
            response.setContent(0);
        }
        return response;
    }

}
