package com.stl.image_generator.api.service;

import reactor.core.publisher.Mono;

public interface IChatGPT {

    ChatGptEditImageResponseDto editImage(Long imageId, Long maskId, String prompt);
}
