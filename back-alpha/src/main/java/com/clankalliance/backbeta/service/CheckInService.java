package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.entity.DateData;

import java.util.List;

public interface CheckInService {

    boolean[] handleFind(List<DateData> dateDataList);
}
