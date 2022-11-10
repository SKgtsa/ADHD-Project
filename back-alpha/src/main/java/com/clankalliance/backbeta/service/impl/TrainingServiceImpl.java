package com.clankalliance.backbeta.service.impl;


import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.repository.TrainingRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.FindGraphResponse;
import com.clankalliance.backbeta.service.TrainingService;

import com.clankalliance.backbeta.utils.HalfTrainingData;
import com.clankalliance.backbeta.utils.TokenUtil;
import com.clankalliance.backbeta.utils.TrainingIdGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
public class TrainingServiceImpl implements TrainingService {

    //完整签到一周可获得的奖励
    private final Integer FULL_WEEK_AWARD = 15;

    //图像的分辨率 代表最终传回前端的图像中点的个数的最大值
    //当图像点数超过该值的1.5倍，会对本图像平均分GRAPH_RESOLUTION端求平均值
    //来获得最终图像
    private final Integer GRAPH_RESOLUTION = 40;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TrainingRepository trainingRepository;

    /**
     * 接收token与原始数据，按照同步的日期来设定时间 按照同步的编号来制定顺序
     * @param token 用户令牌
     * @param rawData 原始数据
     * @return
     */
    @Override
    public CommonResponse handleSave(String token, String rawData) {
        CommonResponse response = new CommonResponse<>();
        //测试后门 上线时会去掉
        if(token.equals("114514")){
            response.setSuccess(true);
            response.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            response = tokenUtil.tokenCheck(token);
        }

        if(!response.getSuccess())
            return response;
        //原本在此去除rawData末端的';' 现废弃，由前端去头去尾
//        rawData = rawData.substring(0, rawData.length() - 2);
        //格式:  编号;金币;图像 对应data[0] data[1] data[2]
        String[] data = rawData.split(";");
        Calendar calendar = Calendar.getInstance();
        //********* 更新属性 对训练平均值和长度进行存储，提高效率***********
        String[] graph = data[2].split(",");
        long total = 0;
        for(String s : graph){
            total += Long.parseLong(s);
        }
        Integer average = Integer.parseInt("" + total/ graph.length);
        //**********************************************************
        //为训练数据制定的更短的id生成规则
        Training training = new Training(TrainingIdGenerator.nextId(response.getMessage()),Integer.parseInt(data[0]), calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE), calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.DAY_OF_WEEK),Integer.parseInt(data[1]), data[2],average, graph.length );
        try{
            trainingRepository.save(training);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存失败");
            return response;
        }
        //由token工具检查token有效后，会一并返回动态存储的用户的id
        //此处使用id 查询到用户 因为此处token工具取得的id可信，不进行错误判断
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingList = user.getTrainingList();
        //由该布尔值记录是否本周满签到
        boolean fullCheckIn = true;
        //满签条件：今天是周日 且训练列表里末尾训练时间为周六 则进一步判断
        //此处训练列表以时间顺序同步，拥有顺序，也能保证查询时最省时
        if(calendar.get(Calendar.DAY_OF_WEEK) == 1 && trainingList.get(trainingList.size() - 1).getDayOfTheWeek() == 7){
            //周日为一周的第一天，故此处减一
            int weekOfTheYear = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
            //当前签到日为周日，不需判断
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
            //完整签到一周
            if(fullCheckIn)
                user.setGold(user.getGold() + FULL_WEEK_AWARD);
        }
        trainingList.add(training);
        user.setTrainingList(trainingList);
        userRepository.save(user);
        response.setMessage("保存成功");
        return response;
    }

    /**
     * 分页获取该用户的所有训练
     * @param token 用户令牌
     * @param pageNum 页码 从1开始
     * @param pageSize 每一页的容量
     * @return
     */
    @Override
    public CommonResponse handleFind(String token, Integer pageNum, Integer pageSize) {
        CommonResponse response = new CommonResponse<>();
        if(token.equals("114514")){
            response.setSuccess(true);
            response.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            response = tokenUtil.tokenCheck(token);
        }
        if(!response.getSuccess())
            return response;
        //由token工具验证token后会以message的方式传出用户id
        //根据id获得用户个体
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingList = user.getTrainingList();
        //该用户的总训练次数
        int totalNum = trainingList.toArray().length;
        //由总训练次数得出的总页数
        int totalPage = totalNum%pageSize == 0?  totalNum / pageSize: totalNum/pageSize + 1;
        //从该位置开始截取子list
        int fromIndex = totalNum - pageNum * pageSize;
        //子list截取到此位置
        int endIndex = fromIndex + pageSize;
        List<Training> result = trainingList.subList( fromIndex >= 0? fromIndex: 0 ,  endIndex);
        //将子list反向，会变为时间倒序
        Collections.reverse(result);
        response.setMessage("查询成功");
        response.setContent(result);
        response.setMessage("" + totalPage);
        return response;
    }

    /**
     * 查询训练对应的图
     * @param token 用户令牌
     * @param id 训练id
     * @return
     */
    @Override
    public FindGraphResponse handleFindGraph(String token, String id) {
        CommonResponse responseT = new CommonResponse<>();
        if(token.equals("114514")){
            responseT.setSuccess(true);
            responseT.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            responseT = tokenUtil.tokenCheck(token);
        }
        FindGraphResponse response = new FindGraphResponse();
        if(!responseT.getSuccess()){
            response.setSuccess(false);
            response.setMessage("登录过期");
            return response;
        }
        //将新令牌赋值给response 传给前端 保存登陆状态
        response.setToken(responseT.getToken());
        //根据训练id查询训练数据
        Optional<Training> top = trainingRepository.findTrainingById(id);
        if(top.isEmpty()){
            response.setMessage("训练不存在");
            response.setSuccess(false);
        }
        //上方进行判断，训练存在，继续进行操作
        Training training = top.get();
        //半成品数据
        String halfResult[] = training.getGraph().split(",");
        int result[];

        //图像数据的时间长度，方便前端处理
        //在前端计算出对应每一点的时间
        int sec = halfResult.length;
        response.setSec(sec);

        //图像过长，以分段取平均值的方式缩短来保证正常显示
        if(halfResult.length > 1.5 * GRAPH_RESOLUTION){
            result = new int[GRAPH_RESOLUTION];
            //每段的长度
            int passageLength = halfResult.length/GRAPH_RESOLUTION;
            //最终结果数组中的遍历器
            int resultIter = 0;
            //原始数据中的遍历器
            int halfIter = 0;
            while(resultIter < result.length){
                //本段累加和
                int num = 0;
                int cache = halfIter;
                //分段求和
                while(halfIter < halfResult.length && halfIter - cache < passageLength){
                    result[resultIter] += Integer.parseInt(halfResult[halfIter]);
                    halfIter ++;
                    num ++;
                }
                //取平均值
                if(num != 0)
                    result[resultIter] /= num;
                resultIter ++;
            }
        }else{
            //图像不长，不需要缩短
            result = new int[halfResult.length];
            for(int i = 0;i < result.length;i ++){
                result[i] = Integer.parseInt(halfResult[i]);
            }
        }


        response.setGraph(result);

        //返回的是本次训练的平均专注度
        response.setConcentration(training.getAverage());
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
        while( iter >= 0 && i >= 0){
            while(iter >= 0 && trainingList.get(iter).getDayOfTheWeek() == dayOfWeek){
                String graph[] = trainingList.get(iter).getGraph().split(",");
                for(String s : graph){
                    totalC += Integer.parseInt(s);
                    totalNum ++;
                }
                result[i] += graph.length;
                iter --;
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
        response.setMessage((totalC > 0? totalC/totalNum : 0) + "");
        return response;
    }

    /**
     * 验证token 根据startIndex 和页长度length
     * 来返回从startIndex开始 有数据的length天的平均数据
     * 相当于返回了个字典，相当于进行了一个预查找
     * @param token 用户令牌
     * @param startIndex 开始位置 对应用户trainingList下标
     * @param length 页长度
     * @return  返回的message为下一页的startIndex 可直接使用
     */
    @Override
    public CommonResponse handleFindDateList(String token,Integer startIndex, Integer length){
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
        HalfTrainingData[] halfTrainingData = new HalfTrainingData[length];
        int trainingListIndex;
        Training target;
        if(startIndex == -1){
            //初次请求，第一次加载页面时的请求
            startIndex = trainingList.size() - 1;
        }
        trainingListIndex = startIndex;
        for(int i = 0;i < length;i ++){
            if(trainingListIndex < 0){
                break;
            }
            target = trainingList.get(trainingListIndex);
            halfTrainingData[i] = new HalfTrainingData(target.getYear(),target.getMonth(),target.getDay(),target.getGold(),0,0,trainingListIndex++,1,0.0);
            //总时间存储计算 用于求平均值
            long totalTime = target.getLength();
            Integer concentration = target.getAverage();
            //存储所有时间数据的list 用于求方差
            List<Integer> timeList = new ArrayList<>(20);
            //加上当天最后一次训练(list中的第一个)
            timeList.add(target.getLength());
            trainingListIndex --;

            for(target = trainingList.get(--trainingListIndex);
                target.getYear() == halfTrainingData[i].getYear()
                    && target.getMonth() == halfTrainingData[i].getMonth()
                    && target.getDay() == halfTrainingData[i].getDay()
                    && trainingListIndex >= 0;
                target = trainingList.get(--trainingListIndex)
            ){
                //总时间(秒)
                totalTime += target.getLength();
                //当天训练次数统计
                halfTrainingData[i].setTrainingNum(halfTrainingData[i].getTrainingNum() + 1);
                //存储本次训练时间，用于求方差
                timeList.add(target.getLength());
                //求总专注度和
                concentration += target.getAverage();
            }
            halfTrainingData[i].setTime(totalTime);
            Double variance = 0.0;
            long averageTime = totalTime / timeList.size();
            //求方差
            for(Integer t : timeList)
                variance += (t - averageTime)*(t - averageTime);
            variance /= timeList.size();
            halfTrainingData[i].setTimeVariance(variance);
            halfTrainingData[i].setTime(totalTime);
            halfTrainingData[i].setConcentrationE(concentration/timeList.size());

        }
        response.setContent(halfTrainingData);
        response.setMessage("" + trainingListIndex);
        return response;
    }

    /**
     * 验证token,根据startIndex来返回这一天的所有训练
     * @param token 用户令牌
     * @param startIndex 开始位置 对应用户trainingList下标
     * @return
     */
    @Override
    public CommonResponse handleFindDateData(String token,Integer startIndex){
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
        int iter = startIndex;
        Training startTraining = trainingList.get(startIndex);
        List<Training> result = new ArrayList<>();
        int y = startTraining.getYear();
        int m = startTraining.getMonth();
        int d = startTraining.getDay();
        Training target;
        for(target = trainingList.get(iter);iter >= 0
                && target.getYear() == y
                && target.getMonth() == m
                && target.getDay() == d;
            target = trainingList.get(--iter)){
            result.add(target);
        }
        response.setContent(result);
        return response;
    }


}
