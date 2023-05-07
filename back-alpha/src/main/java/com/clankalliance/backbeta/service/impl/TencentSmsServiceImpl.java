package com.clankalliance.backbeta.service.impl;

import com.clankalliance.backbeta.response.CommonResponse;
import com.clankalliance.backbeta.service.TencentSmsService;
import com.clankalliance.backbeta.utils.ErrorHandle;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;

// 导入对应SMS模块的client
import com.tencentcloudapi.sms.v20210111.SmsClient;

// 导入要请求接口对应的request response类
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TencentSmsServiceImpl implements TencentSmsService {
    @Value("${tencentCloud.sms.secretId}")
    private String secretId;
    @Value("${tencentCloud.sms.secretKey}")
    private String secretKey;
    @Value("${tencentCloud.sms.appId}")
    private String appId;

    @Value("${tencentCloud.sms.signName}")
    private String signName;
    @Value("${tencentCloud.sms.templateId}")
    private String templateId;
    @Value("${tencentCloud.sms.expireTime}")
    private Integer expireTime;

    @Override
    public CommonResponse sendSms(String phone, String code){
        CommonResponse response = new CommonResponse();
        try {
            Credential credential = new Credential(secretId,secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(expireTime);
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(credential, "ap-beijing",clientProfile);

            SendSmsRequest request = new SendSmsRequest();

            request.setSmsSdkAppId(appId);

            /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
            /*String signName = "签名内容";*/
            request.setSignName(signName);
            /* 国际/港澳台短信 SenderId: 国内短信填空，默认未开通，如需开通请联系 [sms helper] */
            String senderId = "";
            request.setSenderId(senderId);

            /* 用户的 session 内容: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
            String sessionContext = "xxx";
            request.setSessionContext(sessionContext);

            /* 短信号码扩展号: 默认未开通，如需开通请联系 [sms helper] */
            String extendCode = "";
            request.setExtendCode(extendCode);

            /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
            /*String templateId = "400000";*/
            request.setTemplateId(templateId);

            /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
             * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
            /*String[] phoneNumberSet = {"+8621212313123", "+8612345678902", "+8612345678903"};*/
            String[] phoneNumberSet = {"+86"+phone};
            request.setPhoneNumberSet(phoneNumberSet);

            /* 模板参数: 若无模板参数，则设置为空 */
            /*此处的参数必须与申请的的模板参数一一对应，不然报错*/
            String[] templateParamSet = {code};
            request.setTemplateParamSet(templateParamSet);

            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse res = client.SendSms(request);

            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(res));

            // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
            System.out.println(res.getRequestId());
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
            return ErrorHandle.handleSaveException(e,response);
        }
        response.setSuccess(true);
        response.setMessage("验证码已发送");
        return response;
    }
}
