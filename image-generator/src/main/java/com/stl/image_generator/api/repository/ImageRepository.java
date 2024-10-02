package com.stl.image_generator.api.repository;

import com.stl.image_generator.api.entities.CreationType;
import com.stl.image_generator.api.entities.Image;
import com.stl.image_generator.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface  ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findImagesByUser(User user);

    @Query("SELECT i FROM Image i WHERE i.user.uuid = :userUuid AND i.creationType = :creationType")
    List<Image> findImagesByUserAndCreationType(@Param("userUuid") UUID userUuid, @Param("creationType") CreationType creationType);

    // Find an image by User UUID and Image ID
    @Query("SELECT i FROM Image i WHERE i.id = :imageId AND i.user.uuid = :userUuid")
    Optional<Image> findByUserUuidAndImageId(@Param("userUuid") UUID userUuid, @Param("imageId") Long imageId);
}

