package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;

public interface CarService {

    CommonResponse updateDot(String id,int dot);

    CommonResponse upload(String id,String graph,int mark,int gold,int time);

    CommonResponse getDot(String token);

    CommonResponse getDotWithT(String token,int threshold);

    CommonResponse getThreshold(String token);

    CommonResponse updateMap(String token,Integer map);

    CommonResponse addCar(String id, String pass);

    CommonResponse deleteCar(String id, String pass);

    CommonResponse bindCar(String token, String id);
}
