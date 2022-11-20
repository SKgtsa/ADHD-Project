package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.User;
import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.repository.UserRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.CheckInService;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;


@Service
public class CheckInServiceImpl implements CheckInService {


    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private UserRepository userRepository;



    /**
     * 传入训练列表
     * 根据日期判断本周时间范围并检索有效训练，计算签到
     * @param trainingList 对应用户的训练列表
     * @return
     */
    @Override
    public boolean[] handleFind(List<Training> trainingList) {
        boolean checkInWeek[] = {false,false,false,false,false,false,false};
        Calendar calendar = Calendar.getInstance();
        int weekOfTheYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == 1){
            if(trainingList.size() > 0 && trainingList.get(trainingList.size() - 1).getWeekOfTheYear() == weekOfTheYear){
                checkInWeek[6] = true;
            }
            weekOfTheYear --;
        }
        int temp;
        for(int i = trainingList.size() - 1;i >= 0 && trainingList.get(i).getWeekOfTheYear() == weekOfTheYear; i --){
            temp = trainingList.get(i).getDayOfTheWeek() - 2;
            if(temp >= 0){
                checkInWeek[temp] =true;
            }
        }
        return checkInWeek;
    }

}
