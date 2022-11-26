package com.clankalliance.backbeta.controller;


import com.clankalliance.backbeta.request.*;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.FindGraphResponse;
import com.clankalliance.backbeta.response.LastSevenResponse;
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


    @RequestMapping("/save")
    public CommonResponse saveNormal(@RequestBody TrainingSyncRequest request){
        return trainingService.handleSave(request.getToken(), request.getRawData());
    }

//    /**     * 直接分页返回训练数据
//     * @param request
//     * @return
//     */
//    @RequestMapping("/find")
//    public CommonResponse findDirectList(@RequestBody CommonPageableRequest request){
//        return trainingService.handleFind(request.getToken(), request.getPageNum(), request.getSize());
//    }


    @RequestMapping("/findGraph")
    public FindGraphResponse findNormalGraph(@RequestBody CommonFindRequest request){
        return trainingService.handleFindGraph(request.getToken(), request.getId());
    }


    @RequestMapping("/findLastSevenDay")
    public LastSevenResponse findLastSevenDay(@RequestBody TokenCheckRequest request){
        return trainingService.handleFindSeven(request.getToken());
    }

    /**
     * 分页返回日期及统计数据
     * @param request
     * @return
     */
    @RequestMapping("/findDateList")
    public CommonResponse findDateList(@RequestBody CommonPullDownRequest request){
        return trainingService.handleFindDateList(request.getToken(), request.getStartIndex(), request.getLength());
    }

    /**
     * 返回选择日期的所有训练数据
     * @param request
     * @return
     */
    @RequestMapping("/findDateTraining")
    public CommonResponse findDateData(@RequestBody CommonFindRequest request){
        return trainingService.handleFindDateData(request.getToken(), request.getId());
    }


}
