package com.devmaster.demo.day02.dependency_injection;

public class CircleShape implements Shape {
    @Override
    public void draw() {
        System.out.println("CircleShape draw");
    }
}