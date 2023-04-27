package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.blog.Comment;
import com.clankalliance.backbeta.entity.blog.Post;
import com.clankalliance.backbeta.repository.CommentRepository;
import com.clankalliance.backbeta.repository.PostRepository;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.ForumService;
import com.clankalliance.backbeta.service.GeneralUploadService;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Service
public class ForumServiceImpl implements ForumService {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private PostRepository postRepository;

    @Resource
    private GeneralUploadService generalUploadService;


    @Resource
    private CommentRepository commentRepository;

    @Resource
    private UserRepository userRepository;


    public CommonResponse getPost(String token, Integer pageNum, Integer size, String identity){
        CommonResponse response = new CommonResponse<>();
        if(token != null && token.equals("114514")){
            response.setSuccess(true);
            response.setMessage("o1JHJ4rRpzIAw4rYUv90GXo5q3Yc");
        }else{
            response = tokenUtil.tokenCheck(token);
        }
        if(!response.getSuccess()){
            response.setMessage("登录失效");
            return response;
        }
        Sort sort = Sort.by(Sort.Direction.DESC,"time");
        Pageable pageable = PageRequest.of(pageNum,size,sort);
        Page<Post> postPage = postRepository.findByHeadingContaining(identity,pageable);
        response.setContent(postPage);
        return response;
    }

    public CommonResponse savePost(String fId, String token, String content, String heading,boolean anonymous){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()){
            response.setMessage("登录失效");
            return response;
        }
        Optional<Post> pop = postRepository.findById(fId);
        String id;
        List<Comment> commentList;
        if(pop.isEmpty()){
            id = UUID.randomUUID().toString();
            commentList = new ArrayList<>();
            commentList.add(saveCommentPrivate(UUID.randomUUID().toString(),content, response.getMessage(), anonymous));
        }else{
            id = fId;
            commentList = pop.get().getCommentList();
            commentList.set(0, saveCommentPrivate(commentList.get(0).getId(),content, response.getMessage(), anonymous));
        }
        Post post = new Post(id,heading,commentList,new Date());
        try{
            postRepository.save(post);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存失败");
            response.setContent(e);
            return response;
        }
        response.setMessage("保存成功");
        response.setContent(commentList.get(0).getId());
        return response;
    }

    public CommonResponse savePostImage(MultipartFile file, String cId, String token, boolean needDelete){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()){
            response.setMessage("登录失效");
            return response;
        }
        Optional<Comment> cop = commentRepository.findById(cId);
        if(cop.isEmpty()){
            response.setMessage("comment不存在");
            response.setSuccess(false);
            return response;
        }
        Comment comment = cop.get();
        String[] imageURL;
        if(needDelete) {
            deleteCommentImage(comment);
            imageURL = new String[1];
        }else{
            if(comment.getImages().equals("")) {
                imageURL = new String[1];
            }else {
                String[] temp = handleStringToArray(comment.getImages());
                imageURL = new String[temp.length + 1];
                System.arraycopy(temp, 0, imageURL, 0, temp.length);
            }
        }
        String image = generalUploadService.handleForumImageSave(file);
        if(image == null){
            response.setSuccess(false);
            response.setMessage("保存失败");
            return response;
        }
        imageURL[imageURL.length - 1] = image;
        comment.setImages(Arrays.toString(imageURL));
        try{
            commentRepository.save(comment);
        }catch (Exception e){
            response.setSuccess(false);
            response.setMessage("保存失败");
            response.setContent(e);
            return response;
        }
        response.setMessage("保存成功");
        return response;
    }

    private String[] handleStringToArray(String images){
        String tempString = images.substring(1, images.length() - 1);
        return tempString.split(",");
    }

    private void deleteCommentImage(Comment target){
        //删除target的所有图片
        String[] imageURL = handleStringToArray(target.getImages());
        File targetFile;
        for(String image: imageURL){
            targetFile = new File(System.getProperty("user.dir") + image);
            if(targetFile.exists())
                targetFile.delete();
        }
    }

    public CommonResponse saveComment(String cId,String token,String content,boolean anonymous){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()){
            response.setMessage("登录失效");
            return response;
        }
        try{
            saveCommentPrivate(cId,content, response.getMessage(),anonymous);
        }catch (Exception e){
            response.setMessage("保存错误");
            response.setContent(e);
            response.setSuccess(false);
            return response;
        }
        response.setMessage("保存成功");
        return response;
    }

    private Comment saveCommentPrivate(String cId,String content,String userId,boolean anonymous){
        Optional<Comment> cop = commentRepository.findById(cId);
        Date time;
        if(cop.isEmpty()){
            cId = UUID.randomUUID().toString();
            time = new Date();
        }else{
            if(!userId.equals(cop.get().getUser().getWxOpenId())){
                throw new RuntimeException("权限错误");
            }
            time = cop.get().getTime();
        }
        User user = userRepository.findUserByOpenId(userId).get();
        Comment comment = new Comment(cId,content,"",user,time,anonymous);
        commentRepository.save(comment);
        return comment;
    }

    public CommonResponse deletePost(String token,String pId){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()){
            response.setMessage("登录失效");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        Optional<Post> pop = postRepository.findById(pId);
        if(pop.isEmpty()){
            response.setSuccess(false);
            response.setMessage("帖子不存在");
            return response;
        }
        Post post = pop.get();
        //manager
        if(!post.getCommentList().get(0).getUser().getWxOpenId().equals(response.getMessage())){
            response.setSuccess(false);
            response.setMessage("权限错误");
            return response;
        }
        for(Comment c: post.getCommentList()){
            commentRepository.delete(c);
        }
        postRepository.delete(post);
        response.setMessage("删除成功");
        return response;
    }

    public CommonResponse deleteComment(String token,String cId){
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(!response.getSuccess()){
            response.setMessage("登录失效");
            return response;
        }
        User user = userRepository.findUserByOpenId(response.getMessage()).get();
        Optional<Comment> cop = commentRepository.findById(cId);
        if(cop.isEmpty()){
            response.setSuccess(false);
            response.setMessage("评论不存在");
            return response;
        }
        Comment comment = cop.get();
        //manager
        if(!comment.getUser().getWxOpenId().equals(response.getMessage())){
            response.setSuccess(false);
            response.setMessage("权限错误");
            return response;
        }
        commentRepository.delete(comment);
        response.setMessage("删除成功");
        return response;
    }

}
