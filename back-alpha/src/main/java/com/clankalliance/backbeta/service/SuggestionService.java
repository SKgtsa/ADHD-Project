package com.clankalliance.backbeta.service;

import com.clankalliance.backbeta.response.CommonResponse;

public interface SuggestionService {

    CommonResponse handleSuggest(String token,String content);
}
