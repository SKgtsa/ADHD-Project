package com.clankalliance.backbeta.request.Forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePostRequest {
    private String fId;
    private String token;
    private String content;
    private String heading;
    private boolean anonymous;
}
