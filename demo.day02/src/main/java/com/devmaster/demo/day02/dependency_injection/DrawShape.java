package com.devmaster.demo.day02.dependency_injection;

public class DrawShape {
    private Shape shape;

    public DrawShape(Shape shape) {
        this.shape = shape;
    }

    public void Draw() {
        shape.draw();
    }

    public static void main(String[] args) {
        DrawShape drawShape = new DrawShape(new CircleShape());
        drawShape.Draw();
        drawShape = new DrawShape(new RectangleShape());
        drawShape.Draw();
    }
}