package com.clankalliance.backbeta.request.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontUpdateThresholdRequest {

    String token;

    int threshold;

}
