package com.clankalliance.backbeta.utils;

import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Resource
public class RestTemplateUtil {
    /**
    * 设置兼容性-使RestTemplate支持微信后台发送的text/plain反馈
    * @param restTemplate 被调整兼容性的对象
    */
    public static RestTemplate setCapability(RestTemplate restTemplate) {
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        return restTemplate;
    }
}
