package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonFindRequest {

    private String token;

    private String id;

    @Override
    public String toString() {
        return "CommonFindRequest{" +
                "token='" + token + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
