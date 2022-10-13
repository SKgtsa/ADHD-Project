package com.clankalliance.backbeta.utils;


import com.clankalliance.backbeta.response.WXLoginResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Component
public class PostRequestUtils {


    /**
     * 向目的URL发送post请求
     * @param url       目的url
     * @param params    发送的参数
     * @return  ResultVO
     */
    public static WXLoginResponse  sendPostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        //微信返回的Header里的Content-Type值为text/plain
        //RestTemplate 把数据从 HttpResponse 转换成 Object 的时候，找不到合适的 HttpMessageConverter 来转换。
        //此步修正
        client = RestTemplateUtil.setCapability(client);
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        // 执行HTTP请求，将返回的结构使用String类格式化
        ResponseEntity<WXLoginResponse> response = client.exchange(url, method, requestEntity, WXLoginResponse.class);
        return response.getBody();
    }
}
