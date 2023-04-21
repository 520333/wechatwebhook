package com.dawn.webhook.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebUI {
    @GetMapping("/webhook")
    public String param(){
        return "howuse";
    }
}
