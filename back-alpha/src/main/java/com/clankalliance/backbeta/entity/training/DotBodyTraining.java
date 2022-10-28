package com.clankalliance.backbeta.entity.training;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//每次训练的数据实体
@Entity
@Data
@NoArgsConstructor
public class DotBodyTraining {

    @Id
    private String id;

//    //训练开始时间
//    private Date startTime;
//
//    //训练结束时间
//    private Date endTime;
//
//    //专注率
//    private Double concentrationRate;

    private Date startTime;

    private Integer gold;

    @OneToMany
    @JsonIgnore
    private List<Dot> graph;


    public DotBodyTraining(String id, String rawData){
        int iterA = 0;
        int iterB = rawData.indexOf(',', iterA);
        this.gold = Integer.valueOf(rawData.substring(iterA,iterB - 1));
        this.id = id;
        this.graph = new ArrayList<>(300);
        Date time = new Date();
        boolean needTime = true;
        while(iterB != -1){
            //时间 读取年月日时分秒
            iterA = iterB + 1;
            iterB = iterA + 20;
            String timeString = rawData.substring(iterA,iterB - 1);
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            try{
                time = format.parse(timeString);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(needTime){
                this.startTime = time;
                needTime = false;
            }
            //读取专注率
            iterA = iterB + 1;
            iterB = rawData.indexOf(',' , iterA);
            Integer concentrationRate = Integer.valueOf(rawData.substring(iterA, iterB - 1));
            this.graph.add(new Dot(id, concentrationRate, time));
        }
    }
}
