package com.clankalliance.backbeta.utils;

import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class DateDataIdGenerator {

    public static String next(String id){
        Calendar calendar = Calendar.getInstance();
        return id + calendar.get(Calendar.YEAR) + (calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.DAY_OF_MONTH);
    }

}
