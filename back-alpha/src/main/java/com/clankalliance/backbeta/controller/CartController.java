package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.request.cart.CartUploadRequest;
import com.clankalliance.backbeta.request.cart.FrontUpdateThresholdRequest;
import com.clankalliance.backbeta.request.cart.UpdateDotRequest;
import com.clankalliance.backbeta.request.cart.UpdateMapRequest;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Resource
    private CartService cartService;


    @PostMapping("/updateDot")
    public CommonResponse updateDot(@RequestBody UpdateDotRequest request){
        return cartService.updateDot(request.getId(), request.getDot());
    }

    @PostMapping("/upload")
    public CommonResponse updateDot(@RequestBody CartUploadRequest request){
        return cartService.upload(request.getId(), request.getGraph(), request.getMark(), request.getGold(), request.getTime());
    }

    @PostMapping("/getDot")
    public CommonResponse getDot(@RequestBody FrontUpdateThresholdRequest request){
        return cartService.getDot(request.getToken());
    }

    @PostMapping("/getDotWithT")
    public CommonResponse getDotWithT(@RequestBody FrontUpdateThresholdRequest request){
        return cartService.getDotWithT(request.getToken(), request.getThreshold());
    }

    @PostMapping("/getThreshold")
    public CommonResponse getThreshold(@RequestBody FrontUpdateThresholdRequest request){
        return cartService.getThreshold(request.getToken());
    }

    @PostMapping("/updateMap")
    public CommonResponse updateMap(@RequestBody UpdateMapRequest request){
        return cartService.updateMap(request.getToken(),request.getMap());
    }

}
