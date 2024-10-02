package com.stl.image_generator.api.service;

import com.stl.image_generator.api.repository.ImageRepository;
import com.stl.image_generator.exceptions.NotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
public class ChatGPTService implements IChatGPT{

    private final WebClient webClient;

    private ImageRepository imageRepository;

    public ChatGPTService(WebClient webClient, ImageRepository imageRepository){
        this.webClient = webClient;
        this.imageRepository = imageRepository;
    }

    @Override
    public ChatGptEditImageResponseDto editImage(Long imageId, Long maskId, String prompt) {
        return webClient.post()
                .uri("/v1/images/edits")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(buildMultipartBody(imageId, maskId, prompt, 1, "1024x1024"))
                .retrieve()
                .bodyToMono(ChatGptEditImageResponseDto.class)
                .block();

    }
    private MultiValueMap<String, Object> buildMultipartBody(Long imageId, Long maskId, String prompt, int n, String size) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Add the image file
        body.add("image", base64ToByteArrayResource(
                imageRepository.findById(imageId).orElseThrow( ()-> new NotFoundException("Image source not found")).getB64json(),
                UUID.randomUUID().toString() +".png"));

        // Add the mask file
        body.add("mask", base64ToByteArrayResource(
                imageRepository.findById(maskId).orElseThrow( ()-> new NotFoundException("Mask image not found")).getB64json(),
                UUID.randomUUID().toString() +".png"));

        // Add the prompt
        body.add("prompt", prompt);

        // Add the number of images to generate
        body.add("n", String.valueOf(n));

        // Add the size of the images
        body.add("size", size);

        body.add("response_format", "b64_json" );

        return body;
    }

    private ByteArrayResource base64ToByteArrayResource(String base64String, String filename) {
        // Decode the base64 string to a byte array
        byte[] decodedBytes = Base64.getDecoder().decode(base64String.getBytes(StandardCharsets.UTF_8));

        // Create a ByteArrayResource with the decoded bytes and a filename
        return new ByteArrayResource(decodedBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }

}
