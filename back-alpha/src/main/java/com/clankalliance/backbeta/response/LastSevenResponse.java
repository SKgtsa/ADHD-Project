package com.clankalliance.backbeta.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastSevenResponse {

    private Double[] content;

    private Integer average;

    private boolean success;

    private String token;

    private boolean needImage;

    private boolean needComment;

}
