package com.rushbi.dingtalk.utils;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Base64;


public class Authentication {
    /**
     *
     * @param webhook URL
     * @param secretStr 密钥
     * @return URL+timestamp+sign
     * @throws Exception
     */
    public static String GetSign(String webhook,String secretStr) throws Exception {
        Long timestamp = System.currentTimeMillis();
        //定义密钥
        //String secret = "xxxxx";
        //把时间戳和密钥拼接成字符串，中间加入一个换行符
        String stringToSign = timestamp + "\n" + secretStr;
        //声明一个Mac对象，用来操作字符串
        Mac mac = Mac.getInstance("HmacSHA256");
        //初始化Mac对象，设置Mac对象操作的字符串是UTF-8类型，加密方式是SHA256
        mac.init(new SecretKeySpec(secretStr.getBytes("UTF-8"), "HmacSHA256"));
        //把字符串转化成字节形式
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        //新建一个Base64编码对象
        Base64.Encoder encoder = Base64.getEncoder();
        //把上面的字符串进行Base64加密后再进行URL编码
        String sign = URLEncoder.encode(new String(encoder.encodeToString(signData)),"UTF-8");
        //拼接输出时间戳和加密信息
        return String.format("%s&timestamp=%s&sign=%s",webhook, timestamp, sign);
    };
}
