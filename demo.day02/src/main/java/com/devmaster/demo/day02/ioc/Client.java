package com.devmaster.demo.day02.ioc;

public class Client {
    private Service service;

    public Client() {
        service = new Service();
    }

    public void doSomething() {
        service.serve();
    }
}