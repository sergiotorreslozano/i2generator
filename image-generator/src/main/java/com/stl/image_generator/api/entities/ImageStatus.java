package com.stl.image_generator.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ImageStatus {
    @JsonProperty("requested")
    REQUESTED,
    @JsonProperty("generated")
    GENERATED,
    @JsonProperty("error")
    ERROR
}
