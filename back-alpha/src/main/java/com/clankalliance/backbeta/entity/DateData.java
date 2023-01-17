package com.clankalliance.backbeta.entity;

import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.clankalliance.backbeta.utils.DateDataIdGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateData {

    //生成规则：用户id拼接年月日
    @Id
    private String id;

    //当日训练照片名称 统一存放到static/upload/<id>
    private String imageName;

    private Integer year;

    private Integer month;

    private Integer day;

    //训练时间位于一年的第几个周
    private Integer weekOfTheYear;

    //训练时间位于周几
    private Integer dayOfTheWeek;

    private Integer gold;

    //训练时间的方差
    private Double timeVariance;

    //训练总个数
    private Integer trainingNum;

    //平均专注度
    private Integer concentrationE;

    //本日训练时长
    private long time;

    @JsonIgnore
    @OneToMany
    private List<Training> trainingList;

    //用户的评价与感受
    private String comment;

    public DateData(String wxOpenId){
        this.id = DateDataIdGenerator.next(wxOpenId);
        this.imageName = null;
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.weekOfTheYear = calendar.get(Calendar.WEEK_OF_YEAR);
        this.dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);
        this.gold = 0;
        this.timeVariance = 0.0;
        this.trainingNum = 0;
        this.concentrationE = 0;
        this.time = 0;
        this.trainingList = new ArrayList<>();
    }

    public void addTraining(Training t){
        String[] graph = t.getGraph().split(",");
        this.time += graph.length;
        this.gold += t.getGold();
        concentrationE = (concentrationE*trainingNum + t.getAverage())/(++trainingNum);
        trainingList.add(t);
        long temp = 0;
        for(int i = 0;i < trainingNum;i ++){
            temp += (trainingList.get(i).getLength() - concentrationE)*(trainingList.get(i).getLength() - concentrationE);
        }
        timeVariance = Double.valueOf(temp/trainingNum);
    }

}
