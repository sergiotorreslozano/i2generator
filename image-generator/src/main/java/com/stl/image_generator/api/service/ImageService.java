package com.stl.image_generator.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stl.image_generator.dto.ImageGenRequest;
import com.stl.image_generator.api.repository.ImageRepository;
import com.stl.image_generator.api.repository.UserRepository;
import com.stl.image_generator.dto.ImageDto;
import com.stl.image_generator.api.entities.CreationType;
import com.stl.image_generator.api.entities.Image;
import com.stl.image_generator.api.entities.ImageStatus;
import com.stl.image_generator.api.entities.User;
import com.stl.image_generator.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageService implements  IImageService{

    Logger logger = LoggerFactory.getLogger(ImageService.class);

    private ImageRepository imageRepository;
    private UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ImageService(ImageRepository imageRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    public Image saveImage(ImageGenRequest imageGenRequest, CreationType creationType) {
        logger.info("Saving image: " + imageGenRequest.toString() );
        User user = userRepository.getReferenceById(imageGenRequest.userId());
        Image image = new Image.Builder()
                .withPrompt(imageGenRequest.prompt())
                .withUser(user)
                .withCreationType(creationType)
                .withCreatedBy(user.getUuid())
                .withCreatedTime(Instant.now())
                .withImageStatus(ImageStatus.REQUESTED)
                .build();
        return imageRepository.save(image);
    }

    @Override
    public Image saveImage(UUID userId, String prompt, CreationType creationType) {
        logger.info("Saving image for {} {} {} ", userId, prompt, creationType );
        User user = userRepository.findByUuid(userId);
        Image image = new Image.Builder()
                .withPrompt(prompt)
                .withUser(user)
                .withCreationType(creationType)
                .withCreatedBy(user.getUuid())
                .withCreatedTime(Instant.now())
                .withImageStatus(ImageStatus.REQUESTED)
                .build();
        return imageRepository.save(image);
    }

    @Override
    public byte[] transformImage(String b64Json) {
        return Base64.getDecoder().decode(b64Json);
    }

    @Override
    public Image updateImage(Image image, ImageResponse response, UUID user, ImageStatus status) {
        logger.info("Updating image: " + image.getId() );
        Image imageToUpdate = new Image.Builder()
                .withId(image.getId())
                .withPrompt(image.getPrompt())
                .withUser(image.getUser())
                .withCreatedTime(image.getCreatedTime())
                .withCreatedBy(image.getUser().getUuid())
                .withCreationType(image.getCreationType())
                .withModifiedPrompt(parseApiResponse(response))
                .withB64json(response.getResult().getOutput().getB64Json())
                .withUpdateTime(Instant.now())
                .withUpdatedBy(user)
                .withImageStatus(status)
                .build();
        return imageRepository.save(imageToUpdate);
    }

    @Override
    public Image updateImage(Image image, String image64, String modifiedPrompt, UUID user, ImageStatus status) {
        logger.info("Updating image: " + image.getId() );
        Image imageToUpdate = new Image.Builder()
                .withId(image.getId())
                .withPrompt(image.getPrompt())
                .withUser(image.getUser())
                .withCreatedTime(image.getCreatedTime())
                .withCreatedBy(image.getUser().getUuid())
                .withModifiedPrompt(modifiedPrompt)
                .withCreationType(image.getCreationType())
                .withB64json(image64)
                .withUpdateTime(Instant.now())
                .withUpdatedBy(user)
                .withImageStatus(status)
                .build();
        return imageRepository.save(imageToUpdate);
    }

    @Override
    public Image updateImage(Image image, UUID user, ImageStatus status) {
        logger.info("Updating image: " + image.getId() );
        Image imageToUpdate = new Image.Builder()
                .withId(image.getId())
                .withPrompt(image.getPrompt())
                .withUser(image.getUser())
                .withCreatedTime(image.getCreatedTime())
                .withCreatedBy(image.getUser().getUuid())
                .withCreationType(image.getCreationType())
                .withUpdateTime(Instant.now())
                .withUpdatedBy(user)
                .withImageStatus(status)
                .build();
        return imageRepository.save(imageToUpdate);
    }

    @Override
    public Optional<Image> findById(Long id) {
        return imageRepository.findById(id);
    }

    @Override
    public Optional<Image> findByUserUuidAndImageId(UUID userUuid, Long imageId) {
        return imageRepository.findByUserUuidAndImageId(userUuid, imageId);
    }

    @Override
    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    @Override
    public List<ImageDto> findByUserAndCreationType(UUID user, CreationType creationType) {
        List<Image> images = imageRepository.findImagesByUserAndCreationType(user, creationType);
        return images.stream().map(ImageDto::new).collect(Collectors.toList());
    }

    @Override
    public List<ImageDto> findByUser(UUID user) {
        List<Image> images = imageRepository.findImagesByUser(userRepository.findByUuid(user));
        return images.stream().map(ImageDto::new).collect(Collectors.toList());
    }


    @Override
    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }

    @Override
    public ImageDto saveUploadedImage(MultipartFile file, String userId) throws IOException {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(()-> new NotFoundException("User not found"));

        Image image = new Image.Builder()
                .withImageStatus(ImageStatus.GENERATED)
                .withUser(user)
                .withPrompt(file.getOriginalFilename())
                .withB64json(Base64.getEncoder().encodeToString(file.getBytes()))
                .withCreatedBy(UUID.fromString(userId))
                .withCreatedTime(Instant.now())
                .withCreationType(CreationType.USER)
                .build();
        imageRepository.save(image);
        return new ImageDto(image);
    }


    public String parseApiResponse(ImageResponse response) {

        OpenAiImageGenerationMetadata openAiImageGenerationMetadata = (OpenAiImageGenerationMetadata) response.getResult().getMetadata();

        return Optional.ofNullable(openAiImageGenerationMetadata.getRevisedPrompt()).orElse("");

    }
}

