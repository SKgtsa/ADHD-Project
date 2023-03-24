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

    private String id;

    private String content;



}
