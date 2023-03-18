package com.clankalliance.backbeta.request.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartUploadRequest {

    private String id;
    private String graph;
    private int mark;
    private int gold;
    private int time;

}
