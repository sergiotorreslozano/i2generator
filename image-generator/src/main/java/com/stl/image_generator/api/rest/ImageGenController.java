package com.stl.image_generator.api.rest;

import com.stl.image_generator.dto.ImageGenRequest;
import com.stl.image_generator.api.ResponseFormat;
import com.stl.image_generator.api.service.IImageService;
import com.stl.image_generator.dto.ImageDto;
import com.stl.image_generator.api.entities.CreationType;
import com.stl.image_generator.api.entities.Image;
import com.stl.image_generator.api.entities.ImageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@RestController
@Deprecated
public class ImageGenController {

    Logger logger = LoggerFactory.getLogger(ImageGenController.class);

    private final static String INIT_PROMPT = "I NEED to test how the tool works with extremely simple prompts. DO NOT add any detail, just use it AS-IS:";


    private final ImageClient imageClient;
    private final IImageService imageService;

    public ImageGenController(ImageClient imageClient, IImageService imageService){
        this.imageClient = imageClient;
        this.imageService = imageService;
    }

    /**
     * For testing purposes
     * @param imageGenRequest
     * @return a redirect to the image which will be valid for around one hour
     */
    @PostMapping("/api/imagegen")
    public String imageGen (@RequestBody ImageGenRequest imageGenRequest){
        Image image = imageService.saveImage(imageGenRequest, CreationType.AI );
        ImageOptions imageOptions = ImageOptionsBuilder.builder()
                .withModel("dall-e-3")
                .build();
        ImagePrompt imagePrompt = new ImagePrompt(imageGenRequest.prompt(), imageOptions);
        ImageResponse response = imageClient.call(imagePrompt);

        String imageUrl = response.getResult().getOutput().getUrl();
        logger.info(response.getMetadata().toString());
        logger.info(response.getResult().getOutput().getUrl());
        logger.info(response.getResult().getMetadata().toString());
        imageService.updateImage( image, response, imageGenRequest.userId(), ImageStatus.GENERATED);
        return  "redirect:"+imageUrl;
    }

    @PostMapping(value = "/api/simpleimagegen", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<ImageDto> simpleImageGen(@Valid @RequestBody ImageGenRequest imageGenRequest) throws IOException, TimeoutException {
        Image image = imageService.saveImage(imageGenRequest, CreationType.AI );
        ImageOptions imageOptions = ImageOptionsBuilder.builder()
                .withModel("dall-e-3")
                .withResponseFormat(ResponseFormat.B64JSON.toString())
                .build();

        String prompt = INIT_PROMPT + imageGenRequest.prompt();
        ImagePrompt imagePrompt = new ImagePrompt(prompt, imageOptions);

        try {
            ImageResponse response = imageClient.call(imagePrompt);

            logger.info("metadata: " + response.getMetadata().toString());
            logger.info("result.metadata: " + response.getResult().getMetadata().toString());

            // Update image status to GENERATED upon successful generation
            ImageDto imageDto = new ImageDto(imageService.updateImage(image, response, imageGenRequest.userId(), ImageStatus.GENERATED));

            // Return the image as byte array (JPEG format)
            return ResponseEntity.ok(imageDto);

        } catch (HttpStatusCodeException e) {
            // Handle bad authentication or any HTTP error responses
            logger.error("HTTP error occurred: " + e.getMessage(), e);
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                logger.error("Authentication failed. Please check your OpenAI credentials.");
            }
            // Update the image status to ERROR and rethrow the exception
            imageService.updateImage(image, imageGenRequest.userId(), ImageStatus.ERROR);
            throw e; // Rethrow to be handled by GlobalExceptionHandler

        } catch (Exception e) {
            // Handle any other unexpected exceptions
            logger.error("An unexpected error occurred: " + e.getMessage(), e);
            imageService.updateImage(image, imageGenRequest.userId(), ImageStatus.ERROR);
            throw e; // Rethrow to be handled by GlobalExceptionHandler
        }
    }

}
