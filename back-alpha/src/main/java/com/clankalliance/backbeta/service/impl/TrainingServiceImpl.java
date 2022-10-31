package com.clankalliance.backbeta.service.impl;


import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.entity.arrayTraining.TrainingExpired;
import com.clankalliance.backbeta.repository.TrainingExpiredRepository;
import com.clankalliance.backbeta.repository.TrainingRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.TrainingService;

import com.clankalliance.backbeta.utils.TokenUtil;
import com.clankalliance.backbeta.utils.TrainingIdGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;



@Service
public class TrainingServiceImpl implements TrainingService {

    //完整签到一周可获得的奖励
    private final Integer FULL_WEEK_AWARD = 15;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TrainingRepository trainingRepository;

    @Resource
    private TrainingExpiredRepository trainingExpiredRepository;

    @Override
    public CommonResponse handleSaveNormal(String token, String rawData) {
        CommonResponse response = new CommonResponse<>();
        //测试后门
        if(token.equals("114514")){
            response.setSuccess(true);
            response.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            response = tokenUtil.tokenCheck(token);
        }

        if(!response.getSuccess())
            return response;
        rawData = rawData.substring(0, rawData.length() - 2);
        String[] data = rawData.split(";");
        Calendar calendar = Calendar.getInstance();
        Training training = new Training(TrainingIdGenerator.nextId(response.getMessage()),Integer.valueOf(data[0]), calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE), calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.DAY_OF_WEEK),Integer.valueOf(data[1]), data[2] );
        try{
            trainingRepository.save(training);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存失败");
            return response;
        }

        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingList = user.getTrainingList();


        boolean fullCheckIn = true;
        if(calendar.get(Calendar.DAY_OF_WEEK) == 1 && trainingList.get(trainingList.size() - 1).getDayOfTheWeek() == 7){
            //周日为一周第一天，故此处减一
            int weekOfTheYear = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
            boolean weekContainer[] = {false,false,false,false,false,false,true};
            int i = trainingList.size() - 1;
            //倒序遍历本周所有训练
            while(trainingList.get(i).getWeekOfTheYear() == weekOfTheYear){
                weekContainer[trainingList.get(i).getDayOfTheWeek() - 2] = true;
                i--;
            }
            //若有一天未签到，本周不满签
            for(boolean b: weekContainer){
                if(!b)
                    fullCheckIn = false;
            }
        }
        //完整签到一周
        if(fullCheckIn)
            user.setGold(user.getGold() + FULL_WEEK_AWARD);


        trainingList.add(training);
        user.setTrainingList(trainingList);
        userRepository.save(user);
        response.setMessage("保存成功");
        return response;
    }

    @Override
    public CommonResponse handleSaveExpired(String token, String rawData) {
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        rawData = rawData.substring(0, rawData.length() - 2);
        String[] data = rawData.split(";");
        TrainingExpired trainingExpired = new TrainingExpired(TrainingIdGenerator.nextId(response.getMessage()),Integer.valueOf(data[0]),Integer.valueOf(data[1]),data[2]);
        try{
            trainingExpiredRepository.save(trainingExpired);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存失败");
            return response;
        }

        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<TrainingExpired> trainingExpiredList = user.getTrainingExpiredList();
        trainingExpiredList.add(trainingExpired);
        user.setTrainingExpiredList(trainingExpiredList);
        userRepository.save(user);
        response.setMessage("保存成功");
        return response;
    }

    @Override
    public CommonResponse handleFindNormal(String token, Integer pageNum, Integer pageSize) {
        CommonResponse response = new CommonResponse<>();
        if(token.equals("114514")){
            response.setSuccess(true);
            response.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            response = tokenUtil.tokenCheck(token);
        }
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingList = user.getTrainingList();
        int totalNum = trainingList.toArray().length;
        int totalPage = totalNum%pageSize == 0?  totalNum / pageSize: totalNum/pageSize + 1;
        int fromIndex = totalNum - 1 - (pageNum + 1) * pageSize;
        List<Training> result = trainingList.subList( fromIndex >= 0? fromIndex: 0 ,totalNum - pageNum * pageSize);
        Collections.reverse(result);
        response.setMessage("查询成功");
        response.setContent(result);
        response.setMessage("" + totalPage);
        return response;
    }

    @Override
    public CommonResponse handleFindExpired(String token, Integer pageNum, Integer pageSize) {
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<TrainingExpired> trainingExpiredList = user.getTrainingExpiredList();
        int totalNum = trainingExpiredList.toArray().length;
        int totalPage = totalNum%pageSize == 0?  totalNum / pageSize: totalNum/pageSize + 1;
        int fromIndex = totalNum - 1 - (pageNum + 1) * pageSize;
        List<TrainingExpired> result = trainingExpiredList.subList(fromIndex >= 0? fromIndex: 0 ,totalNum - pageNum * pageSize);
        Collections.reverse(result);
        response.setMessage("查询成功");
        response.setContent(result);
        response.setMessage("" + totalPage);
        return response;
    }

    @Override
    public CommonResponse handleFindNormalGraph(String token, String id) {
        CommonResponse response = new CommonResponse<>();
        if(token.equals("114514")){
            response.setSuccess(true);
            response.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            response = tokenUtil.tokenCheck(token);
        }
        if(!response.getSuccess())
            return response;
        Training training = trainingRepository.findTrainingById(id).get();
        String halfResult[] = training.getGraph().split(",");
        int result[] = new int[halfResult.length];
        int total = 0;
        for(int i = 0;i < result.length;i ++){
            result[i] = Integer.valueOf(halfResult[i]);
            total += result[i];
        }
        response.setContent(result);

        //返回的是本次训练的平均专注度
        response.setMessage("" + total / result.length);
        return response;
    }

    @Override
    public CommonResponse handleFindExpiredGraph(String token, String id) {
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        TrainingExpired training = trainingExpiredRepository.findTrainingById(id).get();
        String halfResult[] = training.getGraph().split(",");
        int result[] = new int[halfResult.length];
        int total = 0;
        for(int i = 0;i < result.length;i ++){
            result[i] = Integer.valueOf(halfResult[i]);
            total += result[i];
        }
        response.setContent(result);
        response.setMessage("" + total / result.length);
        return response;
    }

    /**
     * 统计前七天数据：每天训练时间及平均专注率
     * @param token 用户token
     * @return message代表平均专注度，content代表七天图像数据
     */
    @Override
    public CommonResponse handleFindSeven(String token){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingList = user.getTrainingList();

        Double result[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0};

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int iter = trainingList.size() - 1;
        int i = 6;
        //专注度和
        long totalC = 0;
        //专注度数据个数
        long totalNum = 0;
        while(i >= 0){
            while(trainingList.get(iter).getDayOfTheWeek() == dayOfWeek){
                String graph[] = trainingList.get(iter).getGraph().split(",");
                for(String s : graph){
                    totalC += Integer.valueOf(s);
                    totalNum ++;
                }
                result[i] += graph.length;
            }
            if(dayOfWeek == 1){
                dayOfWeek = 7;
            }else{
                dayOfWeek --;
            }
            //化成小时
            result[i] /= 3600;
            i--;
        }
        response.setContent(result);
        response.setToken(totalC/totalNum + "");
        return response;
    }
}
