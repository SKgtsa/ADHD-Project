package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonPullDownRequest {

    private String token;

    private Integer startIndex;

    private Integer endIndex;

    private Integer length;

}
