package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.request.cart.*;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CarService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/api/cart")
public class CarController {

    @Resource
    private CarService carService;


    @PostMapping("/updateDot")
    public CommonResponse updateDot(@RequestBody UpdateDotRequest request){
        System.out.println(request.getDot() + " " + request.getId() + " " + new Date());
        if(request.getId().equals("o1JHJ4mbPNY_gv0RvAWw-_zm_WqM"))
            request.setId("o1JHJ4nlfOb9g5cbztiVQ9piJ85w");
        return carService.updateDot(request.getId(), request.getDot());
    }

    @PostMapping("/upload")
    public CommonResponse upload(@RequestBody CartUploadRequest request){
        if(request.getId().equals("o1JHJ4mbPNY_gv0RvAWw-_zm_WqM"))
            request.setId("o1JHJ4nlfOb9g5cbztiVQ9piJ85w");
        return carService.upload(request.getId(), request.getGraph(), request.getMark(), request.getGold(), request.getTime());
    }

    @PostMapping("/getDot")
    public CommonResponse getDot(@RequestBody FrontUpdateThresholdRequest request){
        return carService.getDot(request.getToken());
    }

    @PostMapping("/getDotWithT")
    public CommonResponse getDotWithT(@RequestBody FrontUpdateThresholdRequest request){
        return carService.getDotWithT(request.getToken(), request.getThreshold());
    }

    @PostMapping("/getThreshold")
    public CommonResponse getThreshold(@RequestBody FrontUpdateThresholdRequest request){
        return carService.getThreshold(request.getToken());
    }

    @PostMapping("/updateMap")
    public CommonResponse updateMap(@RequestBody UpdateMapRequest request){
        return carService.updateMap(request.getToken(),request.getMap());
    }

    @PostMapping("/addCar")
    public CommonResponse addCar(@RequestBody EditRequest request){
        return carService.addCar(request.getId(), request.getPass());
    }

    @PostMapping("/deleteCar")
    public CommonResponse deleteCar(@RequestBody EditRequest request){
        return carService.deleteCar(request.getId(), request.getPass());
    }

    @PostMapping("/bind")
    public CommonResponse bind(@RequestBody BindRequest request){
        return carService.bindCar(request.getToken(), request.getId());
    }

}
