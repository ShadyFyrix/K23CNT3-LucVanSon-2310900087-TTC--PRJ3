package com.devmaster.demo.day02.ioc;

public class IoCWithDI {
    public static void main(String[] args) {
        IoCService service = new IoCService();
        IoCClient client = new IoCClient(service);
        client.doSomething();
    }
}