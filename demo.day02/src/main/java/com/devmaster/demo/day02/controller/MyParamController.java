package com.devmaster.demo.day02.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyParamController {
    @GetMapping("/my-param")
    public String searchUsers(@RequestParam(value = "name", required = false) String name) {
        if (name == null) {
            return "<h2>No name provided, returning all users</h2>";
        }
        return "<h1>Searching for users with name: " + name + "</h1>";
    }

    @GetMapping("/my-variable/{id}")
    public String getUserById(@PathVariable String id) {
        return "<h1>User ID is " + id + "</h1>";
    }
}