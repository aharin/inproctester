package com.thoughtworks.inproctester.jersey.testapp;

public class TestResource {

    private int id;
    private String name;


    public TestResource(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
