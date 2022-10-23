package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.entity.Training;
import com.clankalliance.backbeta.response.CommonResponse;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface TrainingService {

    CommonResponse handleSave(String token, Date startTime, Date endTime, Double concentrationRate);

    CommonResponse handleFind(String token, int pageNum, int pageSize);

}
