package com.stl.image_generator.api;

import com.stl.image_generator.api.repository.ImageRepository;
import com.stl.image_generator.api.repository.UserRepository;
import com.stl.image_generator.api.entities.User;
import com.stl.image_generator.api.entities.UserRole;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class Loader {

    Logger logger = LoggerFactory.getLogger(Loader.class);


    private UserRepository userRepository;
    private ImageRepository imageRepository;

    public Loader(UserRepository userRepository, ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void main (){
        cleanImages();
        UUID admin = createAdminUser();
        populateUser("Batman", admin, UserRole.PREMIUM);
        populateUser("Catwoman", admin,UserRole.PREMIUM);
        populateUser("Joker", admin, UserRole.BASIC);
    }

    private UUID createAdminUser() {
        User admin = userRepository.findByName("Admin");
        UUID creator = UUID.randomUUID();
        if (admin == null){
            admin = User.builder()
                    .withName("Admin")
                    .withIcon("/images/admin.jpg")
                    .withIsActive(false)
                    .withUserRole(UserRole.ADMIN)
                    .withCreatedBy(creator)
                    .withUuid(creator)
                    .withCreatedTime(Instant.now())
                    .build();
            userRepository.save(admin);
        }

        return  admin.getUuid();
    }

    private User populateUser(String name, UUID admin, UserRole role) {
        User user = userRepository.findByName(name);
        if (user ==null){
            User character = User.builder()
                    .withName(name)
                    .withIcon("/images/" +name.toLowerCase() + ".jpg")
                    .withUserRole(role)
                    .withCreatedTime(Instant.now())
                    .withCreatedBy(admin)
                    .build();
            userRepository.save(character);
        }
        return user;
    }

    private void cleanImages() {
        logger.info("Cleaning malformed images");
        imageRepository.findAll().stream().forEach(image -> {
            if (StringUtils.isEmpty(image.getB64json())){
                logger.debug("Deleting image: " + image.getId());
                imageRepository.delete(image);
            }
        });
    }
}
