package com.devmaster.demo.day02.controller;

import com.devmaster.demo.day02.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyBean {
    private final AppConfig appConfig;

    @Autowired
    public MyBean(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping("/my-bean")
    public String myBean() {
        return appConfig.appName();
    }
}