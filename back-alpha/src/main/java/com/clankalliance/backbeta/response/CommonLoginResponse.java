package com.clankalliance.backbeta.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonLoginResponse<T> {

    private boolean success;

    private boolean needRegister;

    private T content;

    private String token;

}
