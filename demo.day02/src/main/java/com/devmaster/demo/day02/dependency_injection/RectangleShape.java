package com.devmaster.demo.day02.dependency_injection;

public class RectangleShape implements Shape {
    @Override
    public void draw() {
        System.out.println("RectangleShape draw");
    }
}