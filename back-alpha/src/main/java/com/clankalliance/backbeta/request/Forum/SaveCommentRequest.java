package com.clankalliance.backbeta.request.Forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCommentRequest {

    private String token;

    private String cId;

    private String pId;

    private String content;

    private boolean anonymous;

}
