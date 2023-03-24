package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ForumService {

    CommonResponse getPost(String token,Integer pageNum,Integer size,String identity);

    CommonResponse savePost(MultipartFile[] files,String fId,String token,boolean withGraph,String trainingId, String content,String heading,boolean anonymous);

    CommonResponse saveComment(MultipartFile[] files,String cId,String token,String content,boolean anonymous);

    CommonResponse deletePost(String token,String pId);

    CommonResponse deleteComment(String token,String cId);

}
