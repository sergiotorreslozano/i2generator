package com.stl.image_generator.api.rest;

import com.stl.image_generator.api.service.ChatGptEditImageResponseDto;
import com.stl.image_generator.api.service.IChatGPT;
import com.stl.image_generator.dto.EditImageDto;
import com.stl.image_generator.dto.ImageGenRequest;
import com.stl.image_generator.api.ResponseFormat;
import com.stl.image_generator.api.service.ImageService;
import com.stl.image_generator.dto.ImageDto;
import com.stl.image_generator.api.entities.CreationType;
import com.stl.image_generator.api.entities.Image;
import com.stl.image_generator.api.entities.ImageStatus;
import com.stl.image_generator.exceptions.NotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ImageController {

    Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final static String INIT_PROMPT = "I NEED to test how the tool works with extremely simple prompts. DO NOT add any detail, just use it AS-IS:";

    private final ImageClient imageClient;
    private final ImageService imageService;
    private final IChatGPT iChatGPT;

    private String base64Image;

    public ImageController(ImageService imageService, ImageClient imageClient, IChatGPT iChatGPT) {
        this.imageService = imageService;
        this.imageClient = imageClient;
        this.iChatGPT = iChatGPT;
    }



    @PostConstruct
    public void loadImage() throws IOException {
        base64Image = loadBase64ImageFromResources();
    }

    @GetMapping("/api/images")
    public List<ImageDto> listImages(){
        return imageService.findAll().stream().map(ImageDto::new).collect(Collectors.toList());
    }

    @GetMapping("/api/images/download/{id}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) throws IOException {
        // Fetch the image by its ID
        Image image = imageService.findById(id).orElseThrow(()-> new NotFoundException("Image not found"));

        if (image == null || image.getB64json() == null) {
            return ResponseEntity.notFound().build();
        }

        // Decode the Base64 string to binary
        byte[] imageBytes = encodeImageInRGBA(image.getB64json());

        // Prepare response with the image bytes
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(ContentDisposition.attachment().filename("image-" + id + ".png").build());

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    private byte[] encodeImageInRGBA(String b64json) throws IOException {
        // Decode the Base64 string to binary
        byte[] imageBytes = Base64.getDecoder().decode(b64json);

        // Convert byte array to BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(bis);

        // Ensure the image is in RGBA format
        BufferedImage rgbaImage = new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Copy the original image into the RGBA formatted image
        Graphics2D graphics = rgbaImage.createGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.dispose();

        // Write the image to a byte array as PNG with RGBA encoding
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(rgbaImage, "png", baos);
        return baos.toByteArray();
    }

    @GetMapping("/api/users/{userId}/images/{imageId}")
    public ResponseEntity<ImageDto> findUserImage(@PathVariable UUID userId, @PathVariable Long imageId) {
        Image image = imageService.findByUserUuidAndImageId(userId, imageId).orElseThrow(()-> new NotFoundException("Image not found"));
        ImageDto imageDto = new ImageDto(image);
        return  ResponseEntity.ok(imageDto);

    }

    @PostMapping(value = "/api/users/{userId}/images", produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageDto> simpleImageGen(@Valid @RequestBody ImageGenRequest imageGenRequest) {
        Image image = imageService.saveImage(imageGenRequest, CreationType.AI );
        ImageOptions imageOptions = ImageOptionsBuilder.builder()
                .withModel("dall-e-3")
                .withResponseFormat(ResponseFormat.B64JSON.toString())
                .build();

        String prompt = INIT_PROMPT + imageGenRequest.prompt();
        ImagePrompt imagePrompt = new ImagePrompt(prompt, imageOptions);

        ImageDto imageDto;
        try {

            if (imageGenRequest.staticImage()) {
                // Logic for static images - Read Base64 from resources
                Image staticImageToUpdate = imageService.updateImage(image, base64Image,imageGenRequest.prompt(), imageGenRequest.userId(), ImageStatus.GENERATED);
                imageDto = new ImageDto(staticImageToUpdate);

            } else {
                // Logic for non-static images
                ImageResponse response = imageClient.call(imagePrompt);
                // Update image status to GENERATED upon successful generation
                Image imageToUpdate = imageService.updateImage(image, response, imageGenRequest.userId(), ImageStatus.GENERATED);
                logger.info("metadata: " + response.getMetadata().toString());
                logger.info("result.metadata: " + response.getResult().getMetadata().toString());
                imageDto = new ImageDto(imageToUpdate);
            }

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

    @DeleteMapping ("/api/images/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id){
        logger.info("Deleting image: {}", id);
        imageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Method to load Base64 image from resources
    // This is to avoid making unnecessary calls to OPEN AI
    private String loadBase64ImageFromResources() throws IOException {
        // Load the image-b64.json file from the resources folder
        ClassPathResource resource = new ClassPathResource("image-b64.json");

        // Convert the content to a String (assumed to be a Base64-encoded JSON file)
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    // New API method to handle image upload
    @PostMapping("/api/users/{userId}/images/upload")
    public ResponseEntity<ImageDto> uploadImage(@PathVariable String userId, @RequestParam("imageFile") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(new ImageDto(), HttpStatus.BAD_REQUEST);
        }
        try {
            ImageDto imageDto = imageService.saveUploadedImage(file, userId); // Save the file using the service
            return new ResponseEntity<>(imageDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ImageDto(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // New API method to handle image edit
    @PostMapping("/api/users/{userId}/images/edit")
    public ResponseEntity<ImageDto> editImage(@PathVariable String userId, @RequestBody EditImageDto editImageDto) {
        logger.info("Editing images {} and mask {} for user {}", editImageDto.getImageId(), editImageDto.getMaskImageId(), userId);
        Image image = imageService.saveImage(UUID.fromString(userId), editImageDto.getPrompt(), CreationType.AI);
        ChatGptEditImageResponseDto responseDtoMono = iChatGPT.editImage(editImageDto.getImageId(), editImageDto.getMaskImageId(), editImageDto.getPrompt());
        responseDtoMono.getData().get(0).getB64_json();
        Image imageToReturn = imageService.updateImage(image, responseDtoMono.getData().get(0).getB64_json(), editImageDto.getPrompt(), UUID.fromString(userId) , ImageStatus.GENERATED);
        return new ResponseEntity<>(new ImageDto(imageToReturn), HttpStatus.OK);
    }

}
