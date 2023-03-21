package com.clankalliance.backbeta.response.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSettingBody {

    private Integer threshold;

    private Integer map;

}
