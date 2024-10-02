package com.stl.image_generator.api.service;

import java.util.List;

public class ChatGptEditImageResponseDto {
    // Root class for the response
    private long created;
    private List<ImageData> data;

    // Getters and Setters
    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public List<ImageData> getData() {
        return data;
    }

    public void setData(List<ImageData> data) {
        this.data = data;
    }


}


