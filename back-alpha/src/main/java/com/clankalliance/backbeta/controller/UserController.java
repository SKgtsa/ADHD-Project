package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.request.InfoUpdateRequest;
import com.clankalliance.backbeta.request.TokenCheckRequest;
import com.clankalliance.backbeta.request.UserLoginRequest;
import com.clankalliance.backbeta.response.CommonLoginResponse;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.MainInfoResponse;
import com.clankalliance.backbeta.service.UserService;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserService userService;


    @PostMapping("/login")
    public CommonLoginResponse login(@RequestBody UserLoginRequest request){
        return userService.handleLogin(request.getCode());
    }


    @PostMapping("/tokenCheck")
    public CommonResponse tokenCheck(@RequestBody TokenCheckRequest request){
        return tokenUtil.tokenCheck(request.getToken());
    }

    @RequestMapping("/findUserInfo")
    public CommonResponse findLastSevenDay(@RequestBody TokenCheckRequest request){
        return userService.handleFindUserInfo(request.getToken());
    }

    @RequestMapping("/findMainInfo")
    public MainInfoResponse findMainInfo(@RequestBody TokenCheckRequest request){
        return userService.handleMainInfo(request.getToken());
    }

    @RequestMapping("/updateInfo")
    public CommonResponse updateInfo(@RequestBody InfoUpdateRequest request){
        return userService.handleUpdate(request.getNickName(),request.getAvatarURL(),request.getToken());
    }

    @RequestMapping("/updateAvatar")
    public CommonResponse updateAvatar(@RequestBody InfoUpdateRequest request){
        return userService.handleUpdateAvatar(request.getAvatarURL(), request.getToken());
    }

    @RequestMapping("/updateNickName")
    public CommonResponse updateNickName(@RequestBody InfoUpdateRequest request){
        return userService.handleUpdateNickName(request.getNickName(), request.getToken());
    }
}
