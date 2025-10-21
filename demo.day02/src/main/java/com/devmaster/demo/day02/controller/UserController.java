package com.devmaster.demo.day02.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public String getUsers() {
        return "<h1>Get all users</h1>";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable int id) {
        return "<h1>User with ID " + id + "</h1>";
    }

    @PostMapping
    public String createUser() {
        return "<h1>User created</h1>";
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable int id) {
        return "<h1>User with ID " + id + " updated</h1>";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id) {
        return "<h1>User with ID " + id + " deleted</h1>";
    }
}