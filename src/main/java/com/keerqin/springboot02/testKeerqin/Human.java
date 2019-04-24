package com.keerqin.springboot02.testKeerqin;

public abstract class Human {
    private int age;
    private String name;

    private void eat() {
        System.out.println("eat");
    }

    abstract void talk();
}
