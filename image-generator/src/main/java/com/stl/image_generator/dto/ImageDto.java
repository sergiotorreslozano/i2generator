package com.stl.image_generator.dto;

import com.stl.image_generator.api.entities.Image;
import com.stl.image_generator.api.entities.ImageStatus;

import java.util.UUID;

public class ImageDto extends BasicDto{
    private Long id;
    private String prompt;
    private String modifiedPrompt;
    private ImageStatus imageStatus;
    private String b64json;

    private UUID userId;  // Optional: just return the user ID instead of the full user object

    public ImageDto(){}

    public ImageDto(Image image) {
        // Basic entity fields
        setCreatedTime(image.getCreatedTime());
        setCreatedBy(image.getCreatedBy());
        setUpdateTime(image.getUpdateTime());
        setUpdatedBy(image.getUpdatedBy());
        this.b64json = image.getB64json();
        this.id = image.getId();
        this.prompt = image.getPrompt();
        this.modifiedPrompt = image.getModifiedPrompt();
        this.imageStatus = image.getImageStatus();
        this.userId = image.getUser() != null ? image.getUser().getUuid() : null;
    }

    public Long getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getModifiedPrompt() {
        return modifiedPrompt;
    }

    public ImageStatus getImageStatus() {
        return imageStatus;
    }

    public String getB64json() {
        return b64json;
    }

    public UUID getUserId() {
        return userId;
    }
}
