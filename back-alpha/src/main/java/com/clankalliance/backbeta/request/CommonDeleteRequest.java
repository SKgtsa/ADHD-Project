package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonDeleteRequest {

    private String token;

    private long id;

    @Override
    public String toString() {
        return "CommonDeleteRequest{" +
                "token='" + token + '\'' +
                ", id=" + id +
                '}';
    }
}
