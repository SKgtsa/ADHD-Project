package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.service.CheckInService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/checkIn")
public class CheckInController {

    @Resource
    private CheckInService checkInService;

    //废除 不再作为单独接口使用

//    @PostMapping("/saveCheckIn")
//    public CommonResponse findCheckIn(@RequestBody TokenCheckRequest request){
//        return checkInService.handleFind(request.getToken());
//    }
}
