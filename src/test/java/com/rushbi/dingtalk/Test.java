package com.rushbi.dingtalk;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.net.URLEncoder;

public class Test {
    public static void main(String[] args) throws Exception {
        //获取时间戳
        Long timestamp = System.currentTimeMillis();
        //定义密钥
        String secret = "xx";
        //把时间戳和密钥拼接成字符串，中间加入一个换行符
        String stringToSign = timestamp + "\n" + secret;
        //声明一个Mac对象，用来操作字符串
        Mac mac = Mac.getInstance("HmacSHA256");
        //初始化Mac对象，设置Mac对象操作的字符串是UTF-8类型，加密方式是SHA256
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        //把字符串转化成字节形式
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        //新建一个Base64编码对象
        Base64.Encoder encoder = Base64.getEncoder();
        //把上面的字符串进行Base64加密后再进行URL编码
        String sign = URLEncoder.encode(new String(encoder.encodeToString(signData)),"UTF-8");
        //分别输出时间戳和加密信息
        System.out.println(timestamp);
        System.out.println(sign);
        String result = String.format("&timestamp=%s&sign=%s", timestamp, sign);
        System.out.println(result);
    }
}