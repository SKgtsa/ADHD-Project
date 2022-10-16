package com.clankalliance.backbeta.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Date time;

    private String content;

    private String wxOpenId;
}
