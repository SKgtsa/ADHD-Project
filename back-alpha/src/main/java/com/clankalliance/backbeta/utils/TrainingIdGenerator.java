package com.clankalliance.backbeta.utils;

import org.springframework.stereotype.Component;

@Component
public class TrainingIdGenerator {
    public String nextId(String wxOpenId){
        return wxOpenId + System.currentTimeMillis();
    }
}
