package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;

public interface CartService {

    CommonResponse updateDot(String id,int dot);

    CommonResponse upload(String id,String graph,int mark,int gold,int time);

    CommonResponse getDot(String token);

    CommonResponse getDotWithT(String token,int threshold);

    CommonResponse getThreshold(String token);

    CommonResponse updateMap(String token,Integer map);

}
