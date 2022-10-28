package com.clankalliance.backbeta.controller;


import com.clankalliance.backbeta.request.CommonFindRequest;
import com.clankalliance.backbeta.request.CommonPageableRequest;
import com.clankalliance.backbeta.request.TrainingSyncRequest;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.TrainingService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Resource
    private TrainingService trainingService;


    @RequestMapping("/saveNormal")
    public CommonResponse saveNormal(@RequestBody TrainingSyncRequest request){
        return trainingService.handleSaveNormal(request.getToken(), request.getRawData());
    }

    @RequestMapping("/saveExpired")
    public CommonResponse saveExpired(@RequestBody TrainingSyncRequest request){
        return trainingService.handleSaveExpired(request.getToken(), request.getRawData());
    }

    @RequestMapping("/findNormal")
    public CommonResponse findNormal(@RequestBody CommonPageableRequest request){
        return trainingService.handleFindNormal(request.getToken(), request.getPageNum(), request.getSize());
    }

    @RequestMapping("/findExpired")
    public CommonResponse findExpired(@RequestBody CommonPageableRequest request){
        return trainingService.handleFindExpired(request.getToken(), request.getPageNum(), request.getSize());
    }

    @RequestMapping("/findNormalGraph")
    public CommonResponse findNormalGraph(@RequestBody CommonFindRequest request){
        return trainingService.handleFindNormalGraph(request.getToken(), request.getId());
    }

    @RequestMapping("/findExpiredGraph")
    public CommonResponse findExpiredGraph(@RequestBody CommonFindRequest request){
        return trainingService.handleFindExpiredGraph(request.getToken(), request.getId());
    }

}
