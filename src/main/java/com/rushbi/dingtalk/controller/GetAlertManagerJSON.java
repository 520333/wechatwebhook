package com.rushbi.dingtalk.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetAlertManagerJSON {
    @RequestMapping(value="/test",consumes = MediaType.APPLICATION_JSON_VALUE)
    public String GetJSON(@RequestBody JsonNode JSON){
        return String.valueOf(JSON);
    }
}
