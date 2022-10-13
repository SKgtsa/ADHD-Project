package com.clankalliance.backbeta.controller;


import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.TencentSmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Resource
    private TencentSmsService tencentSmsService;

    @PostMapping("/send")
    public CommonResponse send(@RequestBody Map<String,String> request){
        System.out.println(request.get("phone") + " and code : " + request.get("code"));
        return tencentSmsService.sendSms(request.get("phone"),request.get("code"));
    }

}
