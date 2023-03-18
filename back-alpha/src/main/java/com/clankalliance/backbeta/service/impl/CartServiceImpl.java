package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.repository.TrainingRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CartService;
import com.clankalliance.backbeta.utils.TokenUtil;
import com.clankalliance.backbeta.utils.TrainingIdGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TrainingRepository trainingRepository;


    public CommonResponse updateDot(String id, int dot){
        CommonResponse response = tokenUtil.updateDot(id,dot);
        if(response.getSuccess()){
            User user = userRepository.findUserByOpenId(id).get();
            response.setContent(user.getThreshold());
        }else{
            response.setContent(-1);
        }
        return response;
    }

    public CommonResponse upload(String id,String graph,int mark,int gold,int time){
        Optional<User> uop = userRepository.findUserByOpenId(id);
        CommonResponse response = new CommonResponse<>();
        if(uop.isEmpty()){
            response.setSuccess(false);
            response.setMessage("用户不存在");
            return response;
        }
        int average = 0;
        String[] graphArray = graph.split(",");
        for(String s : graphArray)
            average += Integer.parseInt(s);
        average /= graphArray.length;
        Training training = new Training(TrainingIdGenerator.nextId(id),mark,gold,graph + ',',average,time);
        try{
            trainingRepository.save(training);
        }catch (Exception e){
            response.setMessage(e.toString());
            response.setSuccess(false);
            return response;
        }
        response.setMessage("上传成功");
        response.setSuccess(true);
        return response;
    }

    public CommonResponse getDot(String token){
        CommonResponse response = tokenUtil.getDot(token);
        if(response.getSuccess())
            response.setMessage("查找成功");
        return response;
    }

    public CommonResponse getDotWithT(String token,int threshold){
        CommonResponse response = tokenUtil.getDot(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        user.setThreshold(threshold);
        try{
            userRepository.save(user);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage(e.toString());
        }
        return response;
    }

    public CommonResponse getThreshold(String token){
        CommonResponse response = tokenUtil.getDot(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        response.setContent(user.getThreshold());
        return response;
    }

}
