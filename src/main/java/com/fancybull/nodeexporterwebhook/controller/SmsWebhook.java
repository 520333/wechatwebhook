package com.fancybull.nodeexporterwebhook.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsWebhook {
    public static String send(String content,String tel){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid",111052);
        jsonObject.put("password", SecureUtil.md5("TaN7gD@V"));
        jsonObject.put("mobile",tel);
        jsonObject.put("msg","【五色神牛】"+content);
        System.out.println(jsonObject.toString());
        return HttpUtil.post("https://submit.10690221.com/send/ordinaryjson",jsonObject.toString());
    }
    @RequestMapping(value = "/sms-webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String handle(@RequestBody JsonNode json){
        JSONObject jsonObject = JSON.parseObject(String.valueOf(json));
        System.out.println(jsonObject.toString());
        return null;
    }

    public static void main(String[] args) {
        send("test","18120660280");
    }
}
