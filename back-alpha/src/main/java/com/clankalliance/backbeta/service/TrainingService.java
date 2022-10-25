package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;

import java.util.Date;

public interface TrainingService {

    CommonResponse handleSave(String token, Date startTime, Date endTime, Double concentrationRate);

    CommonResponse handleFind(String token, int pageNum, int pageSize);

}
