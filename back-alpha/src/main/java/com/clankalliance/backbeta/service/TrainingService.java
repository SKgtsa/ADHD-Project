package com.clankalliance.backbeta.service;


import com.clankalliance.backbeta.response.CommonResponse;

public interface TrainingService {

    CommonResponse handleSaveNormal(String token, String rawData);

    CommonResponse handleSaveExpired(String token, String rawData);

    CommonResponse handleFindNormal(String token, Integer pageNum, Integer pageSize);

    CommonResponse handleFindExpired(String token, Integer pageNum, Integer pageSize);

    CommonResponse handleFindNormalGraph(String token, String id);

    CommonResponse handleFindExpiredGraph(String token, String id);

    CommonResponse handleFindSeven(String token);

}
