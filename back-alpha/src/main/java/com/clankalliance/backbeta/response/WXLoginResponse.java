package com.clankalliance.backbeta.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WXLoginResponse {
    private String session_key;
    private String openid;
}
