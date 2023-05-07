package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.request.CommonPageableRequest;
import com.clankalliance.backbeta.request.Forum.DeleteRequest;
import com.clankalliance.backbeta.request.Forum.SaveCommentRequest;
import com.clankalliance.backbeta.request.Forum.SavePostRequest;
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
    public CommonResponse savePost(@RequestBody SavePostRequest request){
        return forumService.savePost(request.getFId(),request.getToken(),request.getContent(), request.getHeading(), request.isAnonymous());
    }

    @PostMapping("/saveForumImage")
    public CommonResponse savePostImage(HttpSession session,
                                        @RequestParam("file")MultipartFile file,
                                        @RequestParam("cId")String cId,
                                        @RequestParam("token")String token,
                                        @RequestParam("needDelete")String needDelete
    ){
        return forumService.savePostImage(file, cId, token, needDelete.equals("true"));
    }

    @PostMapping("/saveComment")
    public CommonResponse saveComment(@RequestBody SaveCommentRequest request){
        return forumService.saveComment(request.getPId(), request.getCId(),request.getToken(),request.getContent() ,request.isAnonymous());
    }

    @PostMapping("/deleteComment")
    public CommonResponse deleteComment(@RequestBody DeleteRequest request){
        return forumService.deleteComment(request.getToken(), request.getId());
    }


}
