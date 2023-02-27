package com.clankalliance.backbeta.service;


import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.FindGraphResponse;
import com.clankalliance.backbeta.response.LastSevenResponse;
import org.springframework.web.multipart.MultipartFile;


public interface TrainingService {

    Integer getFULL_WEEK_AWARD();

    CommonResponse handleSave(String token, String rawData);


//    CommonResponse handleFind(String token, Integer pageNum, Integer pageSize);


    FindGraphResponse handleFindGraph(String token, String id);


    LastSevenResponse handleFindSeven(String token);

    CommonResponse handleFindDateList(String token,Integer startIndex, Integer length);

    CommonResponse handleFindDateData(String token,String id);

    LastSevenResponse needImageComment(String wxOpenId, LastSevenResponse response);

    CommonResponse handleComment(MultipartFile audioFile, String text, String token);

    Integer getCHECK_IN_MIN_TIME();

}
