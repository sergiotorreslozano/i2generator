package com.stl.image_generator.dto;

import com.stl.image_generator.view.EditImageForm;

public class EditImageDto {
    private Long imageId;
    private Long maskImageId;
    private String prompt;

    public EditImageDto(){}

    public EditImageDto (EditImageForm form){
        this.imageId = form.getImageId();
        this.maskImageId = form.getMaskImageId();
        this.prompt = form.getPrompt();
    }

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
