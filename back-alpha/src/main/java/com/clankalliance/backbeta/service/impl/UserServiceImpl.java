package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonLoginResponse;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.WXLoginResponse;
import com.clankalliance.backbeta.service.UserService;
import com.clankalliance.backbeta.utils.PostRequestUtils;
import com.clankalliance.backbeta.utils.SignatureVerificationUtil;
import com.clankalliance.backbeta.utils.StatusManipulateUtils.ManipulateUtil;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.clankalliance.backbeta.utils.PostRequestUtils.sendPostRequest;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private TokenUtil tokenUtil;


    @Override
    public CommonLoginResponse handleLogin(String code,String signature,String rawData) {
        System.out.println("intoHandleLogin");
        CommonLoginResponse  response = new CommonLoginResponse<>();
        //接受前端code 使用code向微信要openId 以此作为登录凭证，并自动注册
        WXLoginResponse result =  PostRequestUtils.sendPostRequest("https://api.weixin.qq.com/sns/jscode2session?appid=wxacb26bacd3280bd1&secret=b8fe129603fd9cfc2432a3651fc6d07f&js_code=" + code + "&grant_type=authorization_code",null);
        System.out.println("openId: " + result.getOpenid());
        System.out.println("session_key: " + result.getSession_key());
        if(!SignatureVerificationUtil.verify(signature,result.getSession_key(),rawData)){
            response.setSuccess(false);
            return response;
        }
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

}
