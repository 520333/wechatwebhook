package com.rushbi.dingtalk.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationTest {

    @Test
    public void main() throws Exception {
        String sign = Authentication.GetSign("https://oapi.dingtalk.com/robot/send?access_token=2cbeeb30868cb50d04da6a1cedcee332776d5f9d6e51cea814b0ec2c2631cc76xx","xx");
        System.out.println(sign);
    }

}