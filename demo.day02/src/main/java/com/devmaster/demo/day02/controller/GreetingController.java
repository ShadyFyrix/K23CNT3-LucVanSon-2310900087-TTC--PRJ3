package com.devmaster.demo.day02.controller;

import com.devmaster.demo.day02.Service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final GreetingService GreetingService;

    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.GreetingService = greetingService;
    }

    @GetMapping("/greet")
    public String greet() {
        return GreetingService.greet("Shady");
    }
}