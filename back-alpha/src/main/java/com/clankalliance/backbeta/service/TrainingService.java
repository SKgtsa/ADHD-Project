package com.clankalliance.backbeta.service;


import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.response.FindGraphResponse;

public interface TrainingService {

    CommonResponse handleSave(String token, String rawData);


    CommonResponse handleFind(String token, Integer pageNum, Integer pageSize);


    FindGraphResponse handleFindGraph(String token, String id);


    CommonResponse handleFindSeven(String token);

    CommonResponse handleFindDateList(String token,Integer startIndex, Integer length);

    CommonResponse handleFindDateData(String token,Integer startIndex);

}
