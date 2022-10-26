package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;

import java.util.Date;

public interface TrainingService {

    CommonResponse handleSave(String token, String rawData);

    CommonResponse handleFind(String token, int pageNum, int pageSize);

    CommonResponse handleFindGraph(String token, String id);

}
