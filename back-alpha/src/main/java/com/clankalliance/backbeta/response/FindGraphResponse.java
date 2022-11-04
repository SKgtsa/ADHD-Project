package com.clankalliance.backbeta.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindGraphResponse {

    private boolean success;

    private double concentration;

    private int[] graphY;

    private String[] graphX;

    private String token;
}
