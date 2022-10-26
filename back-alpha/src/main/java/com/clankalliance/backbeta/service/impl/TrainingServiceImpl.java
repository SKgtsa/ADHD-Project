package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.training.Training;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.repository.TrainingRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.TrainingService;
import com.clankalliance.backbeta.utils.TokenUtil;
import com.clankalliance.backbeta.utils.TrainingIdGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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

    /**
     * 处理数据同步 所有请求一律视作新训练的保存
     * @param token 用户身份令牌
     * @param rawData 原始数据
     * @return
     */
    @Override
    public CommonResponse handleSave(String token, String rawData){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()) {
            response.setMessage("登录过期");
            return response;
        }
        Training training = new Training(trainingIdGenerator.nextId(response.getMessage()), rawData);
        try{
            trainingRepository.save(training);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存错误");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingSet = user.getTrainingList();
        trainingSet.add(training);
        user.setTrainingList(trainingSet);
        userRepository.save(user);
        response.setMessage("保存成功");
        return response;
    }

    /**
     * 处理find 返回所有训练的时间与金币 不返回图像
     * @param token 用户身份令牌
     * @param pageNum 页码
     * @param pageSize 页容量
     * @return
     */
    @Override
    public CommonResponse handleFind(String token, int pageNum, int pageSize){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()) {
            response.setMessage("登录过期");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingList = user.getTrainingList();
        int totalPage = trainingList.size()/pageSize;
        if(trainingList.size() % pageSize > 0){
            totalPage ++;
        }
        System.out.println("trainingSet.size(): " + trainingList.size());
        System.out.println("pageSize: " + pageSize);
        System.out.println("totalPage: " + totalPage);
        trainingList = trainingList.subList(trainingList.size() - pageSize * (pageNum - 1),trainingList.size() - pageSize * pageNum);
        //颠倒顺序 以时间倒序(添加倒序)送给前端
        Collections.reverse(trainingList);
        response.setContent(trainingList);
        response.setMessage("" + totalPage);
        System.out.println("response.message" + response.getMessage());
        return response;
    }

    @Override
    public CommonResponse handleFindGraph(String token, String id){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()) {
            response.setMessage("登录过期");
            return response;
        }
        Optional<Training> top = trainingRepository.findTrainingById(id);
        if(top.isEmpty()){
            response.setSuccess(false);
            response.setMessage("找不到训练数据");
        }else{
            response.setContent(top.get().getGraph());
        }
        return response;
    }


}
