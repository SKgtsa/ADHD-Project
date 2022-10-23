package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.Training;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.repository.TrainingRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.TrainingService;
import com.clankalliance.backbeta.utils.TokenUtil;
import com.clankalliance.backbeta.utils.TrainingIdGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl implements TrainingService {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private TrainingIdGenerator trainingIdGenerator;

    @Resource
    private TrainingRepository trainingRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    public CommonResponse handleSave(String token, Date startTime, Date endTime, Double concentrationRate){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()) {
            response.setMessage("登录过期");
            return response;
        }
        Training training = new Training(trainingIdGenerator.nextId(response.getMessage()), startTime,endTime,concentrationRate);
        try{
            trainingRepository.save(training);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存错误");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        Set<Training> trainingSet = user.getTrainingSet();
        trainingSet.add(training);
        user.setTrainingSet(trainingSet);
        userRepository.save(user);
        response.setMessage("保存成功");
        return response;
    }

    @Override
    public CommonResponse handleFind(String token, int pageNum, int pageSize){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()) {
            response.setMessage("登录过期");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        Set<Training> trainingSet = user.getTrainingSet();
        int totalPage = trainingSet.size()/pageSize;
        if(trainingSet.size() % pageSize > 0){
            totalPage ++;
        }
        System.out.println("trainingSet.size(): " + trainingSet.size());
        System.out.println("pageSize: " + pageSize);
        System.out.println("totalPage: " + totalPage);
        trainingSet = trainingSet.stream().skip(pageNum * pageSize).limit(pageSize).collect(Collectors.toSet());
        response.setContent(trainingSet);
        response.setMessage("" + totalPage);
        System.out.println("response.message" + response.getMessage());
        return response;
    }

}
