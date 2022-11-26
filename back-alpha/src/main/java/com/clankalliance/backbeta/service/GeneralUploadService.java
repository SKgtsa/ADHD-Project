package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;
import org.springframework.web.multipart.MultipartFile;

//通用文件上传服务
public interface GeneralUploadService {

    CommonResponse handleSave(MultipartFile file, String token, String nickName);

}
