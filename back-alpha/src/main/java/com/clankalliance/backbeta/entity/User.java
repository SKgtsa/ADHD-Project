package com.clankalliance.backbeta.entity;


import com.clankalliance.backbeta.entity.arrayTraining.Training;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(	name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "wxOpenId"),
        })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @JsonIgnore
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private String wxOpenId;

    @JsonIgnore
    private Integer gold;

    private String nickName;

    private String imageURL;

    @JsonIgnore
    @OneToMany
    private List<DateData> dateDataList;

    @JsonIgnore
    private Integer threshold;

    @JsonIgnore
    @Value("${user.map:0}")
    //用户选定的地图编号(0代表未选择地图)
    private Integer map = 0;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL},fetch = FetchType.EAGER)
    private Set<Car> carSet;

//    @JsonIgnore
//    @Value("${user.manager:false}")
//    private boolean isManager = false;

}
