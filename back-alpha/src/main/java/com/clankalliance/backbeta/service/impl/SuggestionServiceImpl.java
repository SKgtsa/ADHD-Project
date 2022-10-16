package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.entity.Suggestion;
import com.clankalliance.backbeta.repository.SuggestionRepository;
import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.SuggestionService;
import com.clankalliance.backbeta.utils.SnowFlake;
import com.clankalliance.backbeta.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private SuggestionRepository suggestionRepository;

    @Override
    public CommonResponse handleSuggest(String token, String content) {
        CommonResponse response = tokenUtil.tokenCheck(token);
        if(response.getSuccess()){
            Suggestion suggestion = new Suggestion(snowFlake.nextId(), content);
            suggestionRepository.save(suggestion);
        }
        return response;
    }
}
