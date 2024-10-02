package com.stl.image_generator.api.rest;


import com.stl.image_generator.api.repository.ImageRepository;
import com.stl.image_generator.api.repository.UserRepository;
import com.stl.image_generator.api.service.ImageService;
import com.stl.image_generator.dto.ImageDto;
import com.stl.image_generator.api.entities.CreationType;
import com.stl.image_generator.api.entities.User;
import com.stl.image_generator.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private UserRepository userRepository;
    private ImageService imageService;

    public UserController(UserRepository userRepository, ImageService imageService){
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    @GetMapping(value = "/api/users")
    public List<UserDto> findUsers(){
        return userRepository.findByIsActiveTrue().stream().map(UserDto::new).collect(Collectors.toList());
    }

    @GetMapping(value = "/api/users/{id}")
    public UserDto findUserById(@PathVariable String id){
        return new UserDto(userRepository.findById(UUID.fromString(id)).orElseThrow());
    }

    @GetMapping(value = "/api/users/{id}/images")
    public List<ImageDto> findAllUserImagesById(@PathVariable String id, @RequestParam Optional<String> createType)  {
        List<ImageDto> images = createType
                .map(type ->imageService.findByUserAndCreationType(UUID.fromString(id), CreationType.valueOf(type)))
                .orElseGet(()-> imageService.findByUser(UUID.fromString(id)));
        return images;
    }
}
