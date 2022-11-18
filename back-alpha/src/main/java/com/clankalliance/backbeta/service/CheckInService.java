package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.response.CommonResponse;

import java.util.List;

public interface CheckInService {

    boolean[] handleFind(List<Training> trainingList);
}
