package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface ForumService {

    CommonResponse getPost(String token,Integer pageNum,Integer size,String identity);

    CommonResponse savePost(String fId,String token, String content,String heading,boolean anonymous);

    CommonResponse savePostImage(MultipartFile file, String cId, String token, boolean needDelete);

    CommonResponse saveComment(String cId,String token,String content,boolean anonymous);

    CommonResponse deletePost(String token,String pId);

    CommonResponse deleteComment(String token,String cId);

}
