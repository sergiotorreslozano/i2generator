package com.stl.image_generator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ImageGenRequest(
        @NotBlank(message = "Prompt cannot be empty") String prompt,
        @NotNull(message = "UserId cannot be null") UUID userId,
        @NotNull(message = "isStatic field cannot be null") Boolean staticImage  // Use Boolean instead of boolean
) {
}
