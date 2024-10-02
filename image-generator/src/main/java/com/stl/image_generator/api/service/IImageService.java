package com.stl.image_generator.api.service;

import com.stl.image_generator.dto.ImageGenRequest;
import com.stl.image_generator.dto.ImageDto;
import com.stl.image_generator.api.entities.CreationType;
import com.stl.image_generator.api.entities.Image;
import com.stl.image_generator.api.entities.ImageStatus;
import org.springframework.ai.image.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IImageService {

    Image saveImage(ImageGenRequest imageGenRequest, CreationType creationType);

    Image saveImage(UUID userId, String prompt, CreationType creationType);
    byte[] transformImage(String b64Json);

    Image updateImage(Image image, ImageResponse response, UUID user, ImageStatus status);

    Image updateImage(Image image, String image64, String modifiedPrompt, UUID user, ImageStatus status);

    Image updateImage (Image image, UUID user, ImageStatus status);

    Optional<Image> findById(Long id);

    Optional<Image> findByUserUuidAndImageId(UUID userUuid, Long imageId);

    List<Image> findAll();

    List<ImageDto> findByUserAndCreationType(UUID user , CreationType creationType);

    List<ImageDto> findByUser(UUID user);
    void deleteById(Long id);

    ImageDto saveUploadedImage(MultipartFile file, String userId) throws IOException;
}
