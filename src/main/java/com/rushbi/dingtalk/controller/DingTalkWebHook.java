package com.rushbi.dingtalk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.rushbi.dingtalk.utils.Authentication;
import com.rushbi.dingtalk.utils.TimeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@RestController
public class DingTalkWebHook {
    @Value("${url}") //webhookURL地址
    private String webhook;
    @Value("${secret}") //钉钉机器人密钥
    private String secret;
    public String MarkDownMsg = null;
    public String MarkDownMsgTitle = null;
    public String resultUrl = null; //鉴权后URL
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/webhook",consumes = MediaType.APPLICATION_JSON_VALUE)
    public String handleDingTalkWebHook(@RequestBody JsonNode AlertManagerJSON){
        String status = null;
        String labels = null;
        String annotations = null;
        String sentMarkdownMsg = null;
        String startsAt= null;
        String endsAt = null;
        try {
            JSONObject jsonObject = JSON.parseObject(String.valueOf(AlertManagerJSON));
            JSONArray alerts = jsonObject.getJSONArray("alerts");
            if (alerts.size() > 0) {
                for (int i = 0; i < alerts.size(); i++) {
                    JSONObject job = alerts.getJSONObject(i);
                    status = job.get("status").toString();
                    labels = job.get("labels").toString();
                    annotations = job.get("annotations").toString();
                    startsAt = job.get("startsAt").toString();
                    endsAt = job.get("endsAt").toString();
                }
            }
            JSONObject label = JSON.parseObject(labels);
            JSONObject annotation = JSON.parseObject(annotations);

            String pattern = "T|\\.\\d+\\D"; //日期正则
            String StartTime = TimeHandler.timeRegx(pattern, " ", startsAt); //告警开始时间
            String EndTime = TimeHandler.timeRegx(pattern, " ", endsAt);  //告警恢复时间

            log.info("告警指标：" + label.getString("job"));
            log.info("告警类型：" + label.getString("alertname"));
            log.info("告警级别：" + label.getString("severity"));
            log.info("主题：" + annotation.getString("summary"));
            log.info("告警详情：" + annotation.getString("description"));
            log.info("告警时间：" + StartTime);
            log.info("恢复时间：" + EndTime);
            log.info(AlertManagerJSON.toString());
            String msgType ; // 消息标题类型
            String resoled; // 恢复时间
            String instance = label.getString("instance");
            String metric = String.format("\n#### <font color=\"#A9A9A9\">告警指标:</font>%s",label.getString("job"));//告警指标
            String alertType = String.format("\n#### <font color=\"#A9A9A9\">告警类型:</font>%s",label.get("alertname"));//告警类型
            String severity = String.format("\n#### <font color=\"#A9A9A9\">告警级别:</font>%s",label.getString("severity")); //告警级别
            String alertSummary = String.format("\n#### <font color=\"#A9A9A9\">主题:</font>" +
                            "\n>##### %s",annotation.getString("summary")); // 告警主题
            String alertDetails = String.format("\n#### <font color=\"#A9A9A9\">告警详情:</font>" +
                    "\n>### <font color=\"#FF0000\">**%s**</font>",annotation.getString("description")); // 告警详情

            String alertStartTime=String.format("\n##### <font color=\"#A9A9A9\">告警时间:</font><font color=\"#FFD700\">%s</font>",StartTime);// 告警开始时间
            if(status.equals("firing")){
                msgType = String.format("%s异常消息", instance);
                resoled = "";
            }else {
                msgType = String.format("%s恢复消息", instance);
                severity= "";
                resoled = String.format("\n##### <font color=\"#A9A9A9\">恢复时间:</font><font color=\"#40E0D0\">**%s**</font>", EndTime);
            }
            MarkDownMsgTitle =String.format(msgType); //markdown消息标题
            MarkDownMsg = String.format("%s%s%s%s%s%s%s",metric,alertType,severity,alertSummary,alertDetails,alertStartTime,resoled);//markdown消息内容
            resultUrl = Authentication.GetSign(webhook,secret);
            log.info(resultUrl);
            //sentMarkdownMsg=senMarkDownMsg(MarkDownMsgTitle,MarkDownMsg);
            return senMarkDownMsg(MarkDownMsgTitle,MarkDownMsg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param title markdown消息标题
     * @param msg   markdown消息内容
     * @return
     * @throws Exception
     */
    public String senMarkDownMsg(String title,String msg) throws Exception {
        JSONObject markdown = new JSONObject();
        markdown.put("title",title);
        markdown.put("text",msg);
        System.out.println(markdown);

        JSONObject reBody = new JSONObject();
        reBody.put("msgtype","markdown");
        reBody.put("markdown",markdown);
        JSONObject at = new JSONObject();
        JSONArray Atr = new JSONArray();
        Atr.add("150xx");
        at.put("atMobiles",Atr);
        at.put("isAtAll",true); //@所有人
        reBody.put("at",at);
        return callWeChatBot(reBody.toString());
    }
    public String callWeChatBot(String reqBody) throws Exception {
        log.info("请求参数：" + reqBody);
        // 构造RequestBody对象，用来携带要提交的数据；需要指定MediaType，用于描述请求/响应 body 的内容类型
        okhttp3.MediaType contentType = okhttp3.MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(contentType, reqBody);
        // 调用群机器人
        String respMsg = okHttp(body, resultUrl);
        if ("0".equals(respMsg.substring(11, 12))) {
            log.info("向群发送消息成功！");
        } else {
            // 发送错误信息到群
            senMarkDownMsg("群机器人推送消息失败，错误信息：\n",respMsg);
        }
        return respMsg;
    }
    public String okHttp(okhttp3.RequestBody body, String url) throws Exception {
        // 构造和配置OkHttpClient
        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS) // 设置读取超时时间
                .build();
        // 构造Request对象
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("cache-control", "no-cache") // 响应消息不缓存
                .build();

        // 构建Call对象，通过Call对象的execute()方法提交异步请求
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 请求结果处理
        assert response != null;
        assert response.body() != null;
        byte[] datas = response.body().bytes();
        String respMsg = new String(datas);
        log.info("返回结果：" + respMsg);
        return respMsg;
    }


}