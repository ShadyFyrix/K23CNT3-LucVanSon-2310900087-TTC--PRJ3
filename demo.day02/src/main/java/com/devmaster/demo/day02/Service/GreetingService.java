package com.devmaster.demo.day02.Service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String greet() {
        return "<h1>Hello from MyGreetingService!</h1>";
    }
}