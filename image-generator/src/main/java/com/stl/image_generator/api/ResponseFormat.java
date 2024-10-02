package com.stl.image_generator.api;

public enum ResponseFormat {

    URL("url"),
    B64JSON("b64_json");

    private final String value;

    // Constructor to initialize the string value for each enum constant
    ResponseFormat(String value) {
        this.value = value;
    }

    // Override the toString() method to return the associated string value
    @Override
    public String toString() {
        return value;
    }

}
