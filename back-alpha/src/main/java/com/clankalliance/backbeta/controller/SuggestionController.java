package com.clankalliance.backbeta.controller;

import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.SuggestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/suggestion")
public class SuggestionController {

    @Resource
    private SuggestionService suggestionService;

    @PostMapping("/suggest")
    public CommonResponse suggest(@RequestBody Map<String,String> request){
        return suggestionService.handleSuggest(request.get("token"), request.get("content"));
    }

}
