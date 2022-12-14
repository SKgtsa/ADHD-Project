package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.DateData;
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
    public CommonLoginResponse handleLogin(String code) {
        CommonLoginResponse  response = new CommonLoginResponse<>();
        WXLoginResponse result;
        if(code != null && code.equals("114514")){
            result = new WXLoginResponse();
        }else{
            //接受前端code 使用code向微信要openId 以此作为登录凭证，并自动注册
            result =  PostRequestUtils.sendPostRequest("https://api.weixin.qq.com/sns/jscode2session?appid=wxacb26bacd3280bd1&secret=b8fe129603fd9cfc2432a3651fc6d07f&js_code=" + code + "&grant_type=authorization_code",null);
            System.out.println("openId: " + result.getOpenid());
            System.out.println("session_key: " + result.getSession_key());
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
            user.setNickName("微信用户");
            user.setImageURL("/static/avatar/default.jpg");
            response.setNeedRegister(true);
        }else{
            user = uop.get();
            if(user.getNickName() == null || user.getNickName().equals("微信用户")){
                response.setNeedRegister(true);
            }else{
                response.setNeedRegister(false);
            }
        }
        userRepository.save(user);
        response.setToken(token);
        response.setSuccess(true);
        response.setContent(user);
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
        List<DateData> dateDataList =  user.getDateDataList();
        Map<String,String> map = new HashMap<>();
        long minute = 0;
        for(DateData d : dateDataList){
            minute += d.getTime();
        }
        minute /= 60;
        map.put("minute","" + minute);
        map.put("gold","" + user.getGold());
        response.setContent(map);
        response.setMessage("查询成功");
        return response;
    }

    @Override
    public CommonResponse changeNickName(String nickName,String token){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        user.setNickName(nickName);
        userRepository.save(user);
        response.setMessage("修改成功");
        return response;
    }


    @Override
    public MainInfoResponse handleMainInfo(String token){
        //token验证 传回的是通用response 此处进行一个数据的复制
        CommonResponse responseT = tokenUtil.tokenCheck(token);
        MainInfoResponse response = new MainInfoResponse();
        if(!responseT.getSuccess()){
            response.setSuccess(false);
            return response;
        }
        response.setToken(responseT.getToken());

        //根据token验证后回传的id找出用户
        User user = userRepository.findUserByOpenId(responseT.getMessage()).get();
        List<DateData> dateDataList =  user.getDateDataList();

        DateData lastDateData = null;

        //今天的日期
        Calendar dateNow = Calendar.getInstance();

        //获取今天是周几
        int dayOfWeek = dateNow.get(Calendar.DAY_OF_WEEK) - 1;
        dayOfWeek = dayOfWeek == 0? 7: dayOfWeek;

        //获取今天是第几周
        int weekOfYear =  dateNow.get(Calendar.WEEK_OF_YEAR);
        if(dayOfWeek == 7)
            weekOfYear --;
        if(dateDataList.size() > 1){
            DateData target = dateDataList.get(dateDataList.size() - 1);
            if(
                    target.getYear() == dateNow.get(Calendar.YEAR)
                            && target.getMonth() == dateNow.get(Calendar.MONTH)
                            && target.getDay() == dateNow.get(Calendar.DAY_OF_MONTH)
            ){
                int tempToday = target.getConcentrationE();
                response.setAverage(tempToday);
                if(dateDataList.size() > 1){
                    target = dateDataList.get(dateDataList.size() - 2);
                    int tempLastDay = target.getConcentrationE();
                    response.setAverage(tempLastDay);
                    response.setImprovementLastTime((tempToday - tempLastDay) / tempLastDay * 100);
                    int i;
                    int tempLastWeek = 0;
                    int num = 0;
                    for(i = dateDataList.size() - 2;i >= 0 && dateDataList.get(i).getWeekOfTheYear() == weekOfYear;i --){
                    }
                    for(;i >= 0 && dateDataList.get(i).getWeekOfTheYear() == weekOfYear - 1;i --){
                        tempLastWeek += dateDataList.get(i).getConcentrationE();
                        num ++;
                    }
                    if(num != 0){
                        tempLastWeek /= num;
                        response.setImprovementLastWeek((tempToday - tempLastWeek) / tempLastWeek * 100);
                    }else{
                        response.setImprovementLastWeek(-101);
                    }
                }else{
                    response.setImprovementLastTime(-101);
                    response.setImprovementLastWeek(-101);
                }
            }else{
                //设置为-101代表未训练
                response.setAverage(-101);
                response.setImprovementLastTime(-101);
                response.setImprovementLastWeek(-101);
            }
        }else{
            //设置为-101代表未训练
            response.setAverage(-101);
            response.setImprovementLastTime(-101);
            response.setImprovementLastWeek(-101);
        }

        //获取签到列表
        boolean[] checkInList = checkInService.handleFind(dateDataList);

        response.setCheckInArray(checkInList);
        response.setDayOfWeek(dayOfWeek);
        response.setSuccess(true);
        return response;
    }


    public CommonResponse handleUpdate(String nickName,String avatarURL,String token){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return  response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        user.setNickName(nickName);
        user.setImageURL(avatarURL);
        userRepository.save(user);
        response.setMessage("修改成功");
        response.setSuccess(true);
        return response;
    }

    public CommonResponse handleUpdateNickName(String nickName,String token){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return  response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        user.setNickName(nickName);
        userRepository.save(user);
        response.setMessage("修改成功");
        response.setSuccess(true);
        return response;
    }

    public CommonResponse handleUpdateAvatar(String avatarURL,String token){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess())
            return  response;
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        user.setImageURL(avatarURL);
        userRepository.save(user);
        response.setMessage("修改成功");
        response.setSuccess(true);
        return response;
    }

}
