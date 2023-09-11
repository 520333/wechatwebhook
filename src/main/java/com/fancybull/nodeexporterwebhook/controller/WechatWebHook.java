package com.fancybull.nodeexporterwebhook.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fancybull.nodeexporterwebhook.pojo.Instance;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class WechatWebHook {
    private final Instance instance = new Instance();
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${url}")
    private String botUrl;// 机器人url地址
    private String startsAt; // 暂停时间
    private String endsAt; // 恢复时间
    // ArrayList<String> userid = new ArrayList<>();
    public WechatWebHook() {}
    public WechatWebHook(String botUrl) {
        this.botUrl = botUrl;
    }

    /**
     * reference: https://www.utctime.net/
     * @param st UTC+0时间
     * @return UTC+8北京时间
     */
    public String timeAdd(String st) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date stParse = simpleDateFormat.parse(st);
        return simpleDateFormat.format((stParse.getTime() + 28800 * 1000));
    }
    /**
     * @param pattern 正则表达式匹配 (T | .000Z)
     * @param replaceText 要替换的文本
     * @param strDateTime 字符串时间 2023-04-21T017:30:00.000Z
     * @return 正则替换后的时间
     */
    public String timeRegx(String pattern,String replaceText,String strDateTime) throws ParseException {
        StringBuffer sb = new StringBuffer();
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strDateTime);
        while (matcher.find()) {
            matcher.appendReplacement(sb, replaceText);
        }
        matcher.appendTail(sb);
        String s = timeAdd(sb.toString());//UTC+8
        log.info("正则替换后的时间：" + sb + " UTC+8北京时间：" + s);
        return timeAdd(sb.toString());
    }

    /**
     * 告警短信接口
     * @param content 短信内容
     * @param tel 手机号码
     * @return POST请求
     */
    public static String sendSMS(String content,String tel){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid",111052);
        jsonObject.put("password", SecureUtil.md5("TaN7gD@V"));
        jsonObject.put("mobile",tel);
        jsonObject.put("msg","【五色神牛】"+content);
        System.out.println(jsonObject.toString());
        // return jsonObject.toJSONString();
        return HttpUtil.post("https://submit.10690221.com/send/ordinaryjson",jsonObject.toString());
    }
    /**
     * @param json alertManager数据
     * @return null
     */
    @RequestMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String handleWeChatHook(@RequestBody JsonNode json) {
        String status = null;
        String labels = null;
        String annotations = null;
        String sentMarkdownMsg = null;
        try {
            JSONObject jsonObject = JSON.parseObject(String.valueOf(json));
            // 整理alerts列表
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
            String reg = "T|\\.\\d+\\D";
            instance.setStartDateTime(timeRegx(reg," ",startsAt));
            instance.setResolvedDateTime(timeRegx(reg," ",endsAt));
            instance.setStatus(status);
            instance.setSeverity(label.getString("severity"));
            instance.setInstance(label.getString("instance"));
            instance.setAlertName(label.getString("alertname"));
            instance.setJob(label.getString("job"));
            instance.setSummary(annotation.getString("summary"));
            instance.setDescription(annotation.getString("description"));
            log.info("告警类型：" + instance.getAlertName());
            log.info("告警级别：" + instance.getSeverity());
            log.info("告警指标：" + instance.getJob());
            log.info("告警实例：" + instance.getInstance());
            log.info("主题：" + instance.getSummary());
            log.info("告警详情：" + instance.getDescription());
            log.info("告警时间：" + instance.getStartDateTime());
            log.info("恢复时间：" + instance.getResolvedDateTime());
            String msgContent="\n告警类型："+instance.getAlertName()+"\n主题："+instance.getSummary()+"\n告警详情："+instance.getDescription();

            String msgType ; // 消息类型
            String color; // font 颜色
            String resoled; // 恢复时间
            String severity = String.format("><font color=\"comment\">告警级别:</font>%s\n",instance.getSeverity());
            if(instance.getStatus().equals("firing")){
                msgType = "异常消息";
                color = "warning";
                resoled = "";
            }else {
                msgType = "恢复消息";
                severity= "";
                color = "info";
                resoled = String.format("<font color=\"comment\">恢复时间:</font><font color=\"info\">%s</font>\n", instance.getResolvedDateTime());
            }
            String markdown = String.format(
                    "# <font color=\"%s\">                    %s</font>\n" +
                            "><font color=\"comment\">告警类型:</font>%s \n" +
                            severity +
                            "><font color=\"comment\">告警指标:</font>%s\n" +
                            "><font color=\"comment\">告警实例:</font>**%s**\n" +
                            "><font color=\"comment\">主题:</font>%s\n" +
                            "><font color=\"comment\">告警详情:</font>\n" +
                            "# `%s`\n" +
                            "\n<font color=\"comment\">告警时间:</font><font color=\"warning\">%s</font>\n" +
                            resoled,
                    color,msgType ,instance.getAlertName(), instance.getJob(),
                    instance.getInstance(), instance.getSummary(),instance.getDescription(),instance.getStartDateTime(),instance.getResolvedDateTime()
            );

            WechatWebHook handleAlert = new WechatWebHook(botUrl);
            // 企业微信告警
            sentMarkdownMsg = handleAlert.sentMarkdownMsg(markdown);
            // 短信告警
            // sendSMS(msgContent,"1812xxx,1305xx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sentMarkdownMsg;
    }

    /**
     * @param msg markdown风格消息
     * @return markdown json
     */
    public String sentMarkdownMsg(String msg) throws Exception {
        // userid.add("@all");
        // userid.add("18120660280");
        JSONObject markdown = new JSONObject();
        markdown.put("content",msg);
        // markdown.put("mentioned_list",userid);
        // markdown.put("mentioned_mobile_list",userid);
        JSONObject reBody = new JSONObject();
        reBody.put("msgtype","markdown");
        reBody.put("markdown",markdown);
        return callWeChatBot(reBody.toString());
    }

    /**
     * 调用群机器人
     * @param reqBody 接口请求参数
     * @throws Exception 可能有IO异常
     * @return 响应消息
     */
    public String callWeChatBot(String reqBody) throws Exception {
        log.info("请求参数：" + reqBody);
        // 构造RequestBody对象，用来携带要提交的数据；需要指定MediaType，用于描述请求/响应 body 的内容类型
        okhttp3.MediaType contentType = okhttp3.MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(contentType, reqBody);
        // 调用群机器人
        String respMsg = okHttp(body, botUrl);
        if ("0".equals(respMsg.substring(11, 12))) {
            log.info("向群发送消息成功！");
        } else {
            // 发送错误信息到群
            sentMarkdownMsg("群机器人推送消息失败，错误信息：\n"+ respMsg);
        }
        return respMsg;
    }

    /**
     * @param body 携带需要提交的数据
     * @param url 请求地址
     * @return 响应消息
     */
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
