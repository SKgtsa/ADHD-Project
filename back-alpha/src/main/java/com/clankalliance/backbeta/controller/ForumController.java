package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.request.CommonDeleteRequest;
import com.clankalliance.backbeta.request.CommonPageableRequest;
import com.clankalliance.backbeta.request.Forum.DeleteRequest;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.ForumService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    private ForumService forumService;

    @PostMapping("/getPost")
    public CommonResponse getPost(@RequestBody CommonPageableRequest request){
        return forumService.getPost(request.getToken(), request.getPageNum(), request.getSize(), request.getIdentity());
    }

    @PostMapping("/savePost")
    public CommonResponse savePost(HttpSession session,
                                   // 路径变量 解决前后端不一致
                                   @RequestParam("file")MultipartFile[] files,
                                   @RequestParam("fId")String fId,
                                   @RequestParam("token")String token,
                                   @RequestParam("withGraph")boolean withGraph,
                                   @RequestParam("trainingId")String trainingId,
                                   @RequestParam("content")String content,
                                   @RequestParam("heading")String heading,
                                   @RequestParam("anonymous")boolean anonymous
                                   ){
        return forumService.savePost(files,fId,token,withGraph,trainingId,content,heading,anonymous);
    }

    @PostMapping("/saveComment")
    public CommonResponse saveComment(HttpSession session,
                                      // 路径变量 解决前后端不一致
                                      @RequestParam("file")MultipartFile[] files,
                                      @RequestParam("cId")String cId,
                                      @RequestParam("token")String token,
                                      @RequestParam("content")String content,
                                      @RequestParam("anonymous")boolean anonymous
    ){
        return forumService.saveComment(files,cId,token,content,anonymous);
    }

    @PostMapping("/deletePost")
    public CommonResponse deletePost(@RequestBody DeleteRequest request){
        return forumService.deletePost(request.getToken(),request.getId());
    }

    @PostMapping("/deleteComment")
    public CommonResponse deleteComment(@RequestBody DeleteRequest request){
        return forumService.deleteComment(request.getToken(), request.getId());
    }


}
