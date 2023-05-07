package com.clankalliance.backbeta.entity;

import com.clankalliance.backbeta.entity.blog.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPage {

    private boolean first;

    private boolean last;

    private List<Post> content;

}
