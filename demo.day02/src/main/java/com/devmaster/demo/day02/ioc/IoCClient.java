package com.devmaster.demo.day02.ioc;

public class IoCClient {
    private IoCService iocService;

    public IoCClient(IoCService service) {
        this.iocService = service;
    }

    public void doSomething() {
        iocService.serve();
    }
}