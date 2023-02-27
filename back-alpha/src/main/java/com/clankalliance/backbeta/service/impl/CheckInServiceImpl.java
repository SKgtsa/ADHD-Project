package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.DateData;
import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CheckInService;
import com.clankalliance.backbeta.service.TrainingService;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;


@Service
public class CheckInServiceImpl implements CheckInService {

    @Resource
    private TrainingService trainingService;

    /**
     * 传入训练列表
     * 根据日期判断本周时间范围并检索有效训练，计算签到
     * @param dateDataList 对应用户的训练日数据列表
     * @return
     */
    @Override
    public boolean[] handleFind(List<DateData> dateDataList) {
        boolean checkInWeek[] = {false,false,false,false,false,false,false};
        Calendar calendar = Calendar.getInstance();
        int weekOfTheYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int index = dateDataList.size() - 1;
        if(dayOfWeek == 1){
            if(dateDataList.size() > 0 && dateDataList.get(index).getWeekOfTheYear() == weekOfTheYear && dateDataList.get(index).getTime() >= trainingService.getCHECK_IN_MIN_TIME()){
                checkInWeek[6] = true;
                index --;
            }
            weekOfTheYear --;
        }
        int temp;
        for(;index >= 0 && dateDataList.get(index).getWeekOfTheYear() == weekOfTheYear; index --){
            temp = dateDataList.get(index).getDayOfTheWeek() - 2;
            if(temp >= 0){
                DateData dateData = dateDataList.get(index);
                if (dateData.getTime() >= trainingService.getCHECK_IN_MIN_TIME()){
                    checkInWeek[temp] =true;
                }
            }
        }
        return checkInWeek;
    }

}
