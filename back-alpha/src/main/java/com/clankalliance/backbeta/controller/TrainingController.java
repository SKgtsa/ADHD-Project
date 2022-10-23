package com.clankalliance.backbeta.controller;


import com.clankalliance.backbeta.entity.Training;
import com.clankalliance.backbeta.request.CommonPageableRequest;
import com.clankalliance.backbeta.request.TrainingSyncRequest;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.TrainingService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Resource
    private TrainingService trainingService;


    @RequestMapping("/save")
    public CommonResponse save(@RequestBody TrainingSyncRequest request){
        return trainingService.handleSave(request.getToken(), request.getStartTime(),request.getEndTime(), request.getConcentrationRate());
    }

    @RequestMapping("/find")
    public CommonResponse find(@RequestBody CommonPageableRequest request){
        return trainingService.handleFind(request.getToken(), request.getPageNum(), request.getSize());
    }

}
