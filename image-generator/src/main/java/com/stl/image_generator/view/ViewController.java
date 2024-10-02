package com.stl.image_generator.view;


import com.stl.image_generator.api.entities.CreationType;
import com.stl.image_generator.dto.EditImageDto;
import com.stl.image_generator.dto.ImageGenRequest;
import com.stl.image_generator.dto.ImageDto;
import com.stl.image_generator.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/view")
public class ViewController {

    Logger logger = LoggerFactory.getLogger(ViewController.class);


    private RestClient restClient;

    private final RestApiProperties restApiProperties;


    public ViewController(RestClient restClient, RestApiProperties restApiProperties){
        this.restClient = restClient;
        this.restApiProperties = restApiProperties;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index(Model model) {
        String usersUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUsersEndpoint())
                .toUriString();
        List<UserDto> users = restClient.get().uri(usersUrl).retrieve().body(List.class);
        model.addAttribute("users", users);
        // Return the name of the Thymeleaf view (index.html)
        return "index";
    }

    // Method to render the "Generate Image" page
    @GetMapping("/users/{id}/generate")
    public String showGeneratePage(@PathVariable("id") String id,Model model) {
        String userByIdUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserByIdEndpoint())
                .buildAndExpand(id)
                .toUriString();
        // Fetch the user using RestClient
        UserDto user = restClient
                .get()
                .uri(userByIdUrl)
                .retrieve()
                .body(UserDto.class);
        model.addAttribute("user", user);
        model.addAttribute("imageForm", new ImageForm());
        return "generate";  // This will resolve to generate.html
    }

    // Method to render the "Generate Image" page
    @GetMapping("/users/{id}/edit")
    public String showEditPage(@PathVariable("id") String id,Model model) {
        model.addAttribute("maskImages", loadUserImages(UUID.fromString(id), CreationType.USER));
        model.addAttribute("images", loadUserImages(UUID.fromString(id), CreationType.AI));
        model.addAttribute("user", loadUser(id));
        model.addAttribute("editImageForm", new EditImageForm());
        return "edit";  // This will resolve to edit.html
    }

    @GetMapping(value = "/users/{id}/list", produces = MediaType.TEXT_HTML_VALUE)
    public String userImages(@PathVariable("id") String id, Model model){

        String userByIdUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserByIdEndpoint())
                .buildAndExpand(id)
                .toUriString();

        // Fetch the user using RestClient
        UserDto user = restClient
                .get()
                .uri(userByIdUrl)
                .retrieve()
                .body(UserDto.class);

        String imageUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserImagesEndpoint())
                .queryParam("createType", CreationType.AI)
                .buildAndExpand(id)
                .toUriString();
        List<ImageDto> images = restClient
                .get()
                .uri(imageUrl)
                .retrieve()
                .body(List.class);

        model.addAttribute("images", images);
        model.addAttribute("user", user);
        model.addAttribute("imageForm", new ImageForm());
        return "list";
    }

    @GetMapping(value = "/users/{id}/mask", produces = MediaType.TEXT_HTML_VALUE)
    public String uploadUserMask(@PathVariable ("id") String id, Model model){
        String userByIdUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserByIdEndpoint())
                .buildAndExpand(id)
                .toUriString();

        // Fetch the user using RestClient
        UserDto user = restClient
                .get()
                .uri(userByIdUrl)
                .retrieve()
                .body(UserDto.class);

        String imageUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserImagesEndpoint())
                .queryParam("createType", CreationType.USER)
                .buildAndExpand(id)
                .toUriString();
        List<ImageDto> images = restClient
                .get()
                .uri(imageUrl)
                .retrieve()
                .body(List.class);

        model.addAttribute("maskImages", loadUserImages(UUID.fromString(id), CreationType.USER));
        model.addAttribute("user", user);
        return "mask";
    }

    @PostMapping(value = "/users/{id}/images", produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public String createUserImage(@PathVariable("id") String id, Model model,
                                   @ModelAttribute ImageGenRequest imageGenRequest){
        String userByIdUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserByIdEndpoint())
                .buildAndExpand(id)
                .toUriString();

        // Fetch the user using RestClient
        UserDto user = restClient
                .get()
                .uri(userByIdUrl)
                .retrieve()
                .body(UserDto.class);

        String imageUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserImagesEndpoint())
                .buildAndExpand(id)
                .toUriString();
        ImageDto image = null;
        try {
             image = restClient.
                    post()
                    .uri(imageUrl)
                    .contentType((MediaType.APPLICATION_JSON))
                    .body(imageGenRequest)
                    .retrieve()
                    .body(ImageDto.class);
        }catch (Exception e){

            logger.error(e.toString());
        }


        model.addAttribute("image", image);
        model.addAttribute("user", user);
        model.addAttribute("imageForm", new ImageForm());
        model.addAttribute("editImageForm", new EditImageForm());
        return "userImages";
    }

    @PostMapping("/simpleimagegen")
    public String generateImage(@ModelAttribute ImageForm imageForm, Model model) {
        // Here, you can call the API to generate the image using RestTemplate or WebClient

        String generateSimpleImage = UriComponentsBuilder
                .fromPath(restApiProperties.getImageServiceProperties().getGenerateSimpleImagesEndpoint())
                .toUriString();

        // Fetch the user using RestClient
        String response = restClient
                .post()
                .uri(generateSimpleImage)
                .body(imageForm)
                .retrieve()
                .body(String.class);

        return "redirect:/view/users/" + imageForm.getUserId() + "/images";
    }

    @DeleteMapping ("/users/{userId}/images/{imageId}")
    public String deleteImage (@PathVariable UUID userId, @PathVariable Long imageId, Model model){
        logger.info("Deleting image {}", imageId);
        String deleteImageUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getImageServiceProperties().getImageByIdEndpoint())
                .buildAndExpand(imageId)
                .toUriString();
        String response = restClient
                .delete()
                .uri(deleteImageUrl)
                .retrieve()
                .body(String.class);
        model.addAttribute("user", loadUser(userId.toString()));
        model.addAttribute("maskImages", loadUserImages(userId, CreationType.USER));
        return "list";
    }

    @DeleteMapping ("/users/{userId}/masks/{imageId}")
    public String deleteMask (@PathVariable UUID userId, @PathVariable Long imageId, Model model){
        logger.info("Deleting image {}", imageId);
        String deleteImageUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getImageServiceProperties().getImageByIdEndpoint())
                .buildAndExpand(imageId)
                .toUriString();
        String response = restClient
                .delete()
                .uri(deleteImageUrl)
                .retrieve()
                .body(String.class);
        model.addAttribute("user", loadUser(userId.toString()));
        model.addAttribute("maskImages", loadUserImages(userId, CreationType.USER));
        return "mask";
    }

    private List<ImageDto> loadUserImages(UUID userId, CreationType creationType) {
        String imageUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserImagesEndpoint())
                .queryParam("createType", creationType)
                .buildAndExpand(userId)
                .toUriString();
        List<ImageDto> images = restClient.
                get()
                .uri(imageUrl)
                .retrieve()
                .body(List.class);

        return  images;
    }

    private UserDto loadUser(String userId){
        String userByIdUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getUserServiceProperties().getUserByIdEndpoint())
                .buildAndExpand(userId)
                .toUriString();
        // Fetch the user using RestClient
        UserDto user = restClient
                .get()
                .uri(userByIdUrl)
                .retrieve()
                .body(UserDto.class);
        return user;
    }

    @PostMapping("/users/{userId}/images/upload")
    public String uploadImage(@PathVariable String userId,
                              @RequestParam("imageFile") MultipartFile file,
                              RedirectAttributes redirectAttributes) {

        // Check if the file is empty
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/view/users/" + userId + "/mask";
        }

        try {
            // Convert MultipartFile to a File resource or InputStreamResource
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Using InputStreamResource to handle the MultipartFile input
            body.add("imageFile", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create an HttpEntity with the body and headers
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Construct the upload URL
            String uploadImagesUrl = UriComponentsBuilder
                    .fromPath(restApiProperties.getImageServiceProperties().getUserImagesUploadEndpoint())
                    .buildAndExpand(userId)
                    .toUriString();

            // Send the file to the REST endpoint using RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ImageDto> response = restTemplate.postForEntity(restApiProperties.getUserServiceProperties().getBaseUrl() +uploadImagesUrl,
                    requestEntity, ImageDto.class);

            // Check the response and handle it
            if (response.getStatusCode().is2xxSuccessful()) {
                ImageDto uploadedImage = response.getBody();
                redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'");
                redirectAttributes.addFlashAttribute("uploadedImage", uploadedImage);
            } else {
                redirectAttributes.addFlashAttribute("message", "Failed to upload image. Status: " + response.getStatusCode());
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload file: " + e.getMessage());
        }

        return "redirect:/view/users/" + userId + "/mask";
    }


    @PostMapping("/users/{id}/images/edit")
    public String submitSelection(@PathVariable String id, @ModelAttribute EditImageForm editImageForm, Model model) {
        // Process the submitted data as required
        logger.info("Editing images {} and mask {} for user {}", editImageForm.getImageId(), editImageForm.getMaskImageId(), id);

        String editImageIdUrl = UriComponentsBuilder
                .fromPath(restApiProperties.getImageServiceProperties().getUserImagesEditEndpoint())
                .buildAndExpand(id)
                .toUriString();
        // Fetch the user using RestClient
        ImageDto imageDto = restClient
                .post()
                .uri(editImageIdUrl)
                .body(new EditImageDto(editImageForm))
                .retrieve()
                .body(ImageDto.class);


        model.addAttribute("editImageForm", editImageForm.getImageId());
        model.addAttribute("images", loadUserImages(UUID.fromString(id), CreationType.AI));
        model.addAttribute("maskImages", loadUserImages(UUID.fromString(id), CreationType.USER));
        model.addAttribute("user", loadUser(id));
        model.addAttribute("editedImage", imageDto);
        return "redirect:/view/users/" + id + "/edit";
    }


}