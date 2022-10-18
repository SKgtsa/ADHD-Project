package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.request.TokenCheckRequest;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CheckInService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/checkIn")
public class CheckInController {

    @Resource
    private CheckInService checkInService;

    @PostMapping("/saveCheckIn")
    public CommonResponse saveCheckIn(@RequestBody TokenCheckRequest request){
        return checkInService.handleSave(request.getToken());
    }

}
