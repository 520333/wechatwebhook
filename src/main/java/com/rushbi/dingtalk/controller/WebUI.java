package com.rushbi.dingtalk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebUI {
    @GetMapping("/")
    public String param(){
        return "index";
    }
}
