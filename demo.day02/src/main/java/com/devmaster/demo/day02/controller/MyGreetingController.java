package com.devmaster.demo.day02.controller;

import com.devmaster.demo.day02.Service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyGreetingController {
    private final GreetingService myGreetingService;

    @Autowired
    public MyGreetingController(GreetingService greetingService) {
        this.myGreetingService = greetingService;
    }

    @GetMapping("/my-greet")
    public String greet() {
        return myGreetingService.greet();
    }
}