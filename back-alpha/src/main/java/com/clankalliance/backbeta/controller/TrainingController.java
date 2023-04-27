package com.clankalliance.backbeta.controller;


import com.clankalliance.backbeta.request.*;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.FindGraphResponse;
import com.clankalliance.backbeta.response.LastSevenResponse;
import com.clankalliance.backbeta.response.TestResponse;
import com.clankalliance.backbeta.service.TrainingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 接收音频与文本 保存并与今日训练建立联系
     * @param audioFile
     * @param text
     * @param token
     * @return
     */
    @RequestMapping("/saveComment")
    public CommonResponse saveComment(@RequestParam("audio") MultipartFile audioFile, @RequestParam("text") String text, @RequestParam("token") String token){
        return trainingService.handleComment(audioFile, text, token);
    }

    /**
     * 只上传文本
     * @return
     */
    @RequestMapping("/saveCommentText")
    public CommonResponse saveCommentText(@RequestBody SaveCommentTextRequest request){
        return trainingService.handleComment(null, request.getText(), request.getToken());
    }

    @RequestMapping("/test")
    public TestResponse test(@RequestBody TestRequest request){
        System.out.println("测试接口收到数据如下");
        System.out.println(request);
        return new TestResponse("test_ok",System.currentTimeMillis());
    }

    @GetMapping("/testget")
    public TestResponse testGet(@RequestBody TestRequest request){
        System.out.println("get测试接口收到数据如下");
        System.out.println(request);
        return new TestResponse("test_ok",System.currentTimeMillis());
    }

    @PostMapping("/testpost")
    public TestResponse testPost(@RequestBody TestRequest request){
        System.out.println("post测试接口收到数据如下");
        System.out.println(request);
        return new TestResponse("test_ok",System.currentTimeMillis());
    }


    @GetMapping("/testgetn")
    public TestResponse testNoValueGet(){
        System.out.println("无参数get接口收到请求");
        return new TestResponse("test_ok",System.currentTimeMillis());
    }

    @PostMapping("/testpostn")
    public TestResponse testNoValuePost(){
        System.out.println("无参数post接口收到请求");
        return new TestResponse("test_ok",System.currentTimeMillis());
    }

}
