package com.stl.image_generator.view;

public class EditImageForm {
    private Long imageId;
    private Long maskImageId;
    private String prompt;

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Long getMaskImageId() {
        return maskImageId;
    }

    public void setMaskImageId(Long maskImageId) {
        this.maskImageId = maskImageId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
