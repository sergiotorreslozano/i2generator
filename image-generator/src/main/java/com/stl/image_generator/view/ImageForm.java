package com.stl.image_generator.view;

public class ImageForm {
    private String prompt;
    private String userId;
    private boolean staticImage; // New field for 'static'

    // Getters and setters
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isStaticImage() {
        return staticImage;
    }

    public void setStaticImage(boolean staticImage) {
        this.staticImage = staticImage;
    }
}
