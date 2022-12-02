package com.clankalliance.backbeta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoUpdateRequest {

    public String avatarURL;

    public String nickName;

    public String token;

}
