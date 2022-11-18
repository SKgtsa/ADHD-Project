package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonLoginResponse;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.MainInfoResponse;
import com.clankalliance.backbeta.response.WXLoginResponse;
import com.clankalliance.backbeta.service.CheckInService;
import com.clankalliance.backbeta.service.TrainingService;
import com.clankalliance.backbeta.service.UserService;
import com.clankalliance.backbeta.utils.HalfTrainingData;
import com.clankalliance.backbeta.utils.PostRequestUtils;
import com.clankalliance.backbeta.utils.SignatureVerificationUtil;
import com.clankalliance.backbeta.utils.StatusManipulateUtils.ManipulateUtil;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.clankalliance.backbeta.utils.PostRequestUtils.sendPostRequest;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private CheckInService checkInService;

    @Resource
    private TrainingService trainingService;

    @Override
    public CommonLoginResponse handleLogin(String code,String signature,String rawData) {
        CommonLoginResponse  response = new CommonLoginResponse<>();
        WXLoginResponse result;
        if(code.equals("114514")){
            result = new WXLoginResponse();
            result.setOpenid(signature);
        }else{
            //接受前端code 使用code向微信要openId 以此作为登录凭证，并自动注册
            result =  PostRequestUtils.sendPostRequest("https://api.weixin.qq.com/sns/jscode2session?appid=wxacb26bacd3280bd1&secret=b8fe129603fd9cfc2432a3651fc6d07f&js_code=" + code + "&grant_type=authorization_code",null);
            System.out.println("openId: " + result.getOpenid());
            System.out.println("session_key: " + result.getSession_key());
            if(!SignatureVerificationUtil.verify(signature,result.getSession_key(),rawData)){
                response.setSuccess(false);
                return response;
            }
        }
        System.out.println("intoHandleLogin");
        Optional<User> uop = userRepository.findUserByOpenId(result.getOpenid());


        //更新状态
        ManipulateUtil.updateStatus(result.getOpenid());
        //从尾节点获取token
        String token = ManipulateUtil.endNode.getToken();
        User user;
        if(uop.isEmpty()){
            //首次登陆 进行新用户信息的创建
            user = new User();
            user.setWxOpenId(result.getOpenid());
            user.setGold(0);
            response.setNeedRegister(true);
        }else{
            user = uop.get();
            response.setNeedRegister(false);
        }
        userRepository.save(user);
        response.setToken(token);
        response.setSuccess(true);
        System.out.println(response);
        return response;
    }

    @Override
    public CommonResponse handleFindUserInfo(String token){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()){
            response.setMessage("登陆失效");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        List<Training> trainingList =  user.getTrainingList();
        Map<String,String> map = new HashMap<>();
        long hour = 0;
        for(Training t : trainingList){
            hour += t.getGraph().split(",").length;
        }
        hour /= 3600;
        map.put("hour","" + hour);
        map.put("gold","" + user.getGold());
        response.setContent(map);
        response.setMessage("查询成功");
        return response;
    }

    @Override
    public MainInfoResponse handleMainInfo(String token){
        CommonResponse responseT = tokenUtil.tokenCheck(token);
        MainInfoResponse response = new MainInfoResponse();
        if(!responseT.getSuccess()){
            response.setSuccess(false);
            return response;
        }
        response.setToken(responseT.getToken());
        User user = userRepository.findUserByOpenId(responseT.getMessage()).get();
        List<Training> trainingList = user.getTrainingList();

        HalfTrainingData halfTrainingData = trainingService.handleFindLastDate(trainingList);

        Calendar dateNow = Calendar.getInstance();
        Calendar dateLastTraining = Calendar.getInstance();
        Integer days;
        if(halfTrainingData == null){
            days = -1;
        }else{
            dateLastTraining.set(halfTrainingData.getYear(),halfTrainingData.getMonth() - 1,halfTrainingData.getDay());
            Date timeNow = new Date();
            Date timeLastTraining = dateLastTraining.getTime();
            days = Integer.parseInt( "" + (timeNow.getTime() - timeLastTraining.getTime())/(1000*60*60*24));
        }
        switch (days){
            case 0:
                response.setLastTrainingTime("今天训练了");
                break;
            case 1:
                response.setLastTrainingTime("昨天训练了");
                break;
            case 2:
                response.setLastTrainingTime("前天训练了");
                break;
            case -1:
                response.setLastTrainingTime("您还未训练过");
                break;
            default:
                response.setLastTrainingTime(days + "天前训练了");
                break;
        }

        boolean[] checkInList = checkInService.handleFind(trainingList);
        int missNum = 0;
        int needNum = 0;
        int dayOfWeek = dateNow.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek == 0? 7: dayOfWeek;
        for(int i = 0;i < 7;i ++){
            if(!checkInList[i]){
                if(i > dayOfWeek + 1){
                    //今天及以后的 为未签到
                    needNum ++;
                }else if(i < dayOfWeek + 1){
                    //今天以前的为漏签
                    missNum ++;
                }
            }
        }
        if(missNum == 0){
            if(dayOfWeek == 7){
                response.setCheckInLetter("本周满签!获得" + trainingService.getFULL_WEEK_AWARD() + "金币");
            }else{
                response.setCheckInLetter("距离满签还有" + needNum + "天");
            }
        }else{
            response.setCheckInLetter("漏签了，请下周努力");
        }

        response.setCheckInArray(checkInList);
        response.setDayOfWeek(dayOfWeek);
        response.setLastDateTraining(halfTrainingData);
        return response;
    }

}
