package com.stl.image_generator.view;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rest-api")
public class RestApiProperties {

    private UserServiceProperties userServiceProperties;
    private ImageServiceProperties imageServiceProperties;


    // Getters and Setters

    public UserServiceProperties getUserServiceProperties() {
        return userServiceProperties;
    }

    public void setUserServiceProperties(UserServiceProperties userServiceProperties) {
        this.userServiceProperties = userServiceProperties;
    }

    public ImageServiceProperties getImageServiceProperties() {
        return imageServiceProperties;
    }

    public void setImageServiceProperties(ImageServiceProperties imageServiceProperties) {
        this.imageServiceProperties = imageServiceProperties;
    }

    public static class UserServiceProperties {
        private String baseUrl;
        private String usersEndpoint;
        private String userByIdEndpoint;
        private String userImagesEndpoint;

        // Getters and Setters

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getUsersEndpoint() {
            return usersEndpoint;
        }

        public void setUsersEndpoint(String usersEndpoint) {
            this.usersEndpoint = usersEndpoint;
        }

        public String getUserByIdEndpoint() {
            return userByIdEndpoint;
        }

        public void setUserByIdEndpoint(String userByIdEndpoint) {
            this.userByIdEndpoint = userByIdEndpoint;
        }

        public String getUserImagesEndpoint() {
            return userImagesEndpoint;
        }

        public void setUserImagesEndpoint(String userImagesEndpoint) {
            this.userImagesEndpoint = userImagesEndpoint;
        }
    }

    public static class ImageServiceProperties {
        private String baseUrl;
        private String generateSimpleImagesEndpoint;
        private String imageByIdEndpoint;
        private String userImagesUploadEndpoint;
        private String userImagesEditEndpoint;
        // Getters and Setters

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getGenerateSimpleImagesEndpoint() {
            return generateSimpleImagesEndpoint;
        }

        public void setGenerateSimpleImagesEndpoint(String generateSimpleImagesEndpoint) {
            this.generateSimpleImagesEndpoint = generateSimpleImagesEndpoint;
        }

        public String getImageByIdEndpoint() {
            return imageByIdEndpoint;
        }

        public void setImageByIdEndpoint(String imageByIdEndpoint) {
            this.imageByIdEndpoint = imageByIdEndpoint;
        }

        public String getUserImagesUploadEndpoint() {
            return userImagesUploadEndpoint;
        }

        public void setUserImagesUploadEndpoint(String userImagesUploadEndpoint) {
            this.userImagesUploadEndpoint = userImagesUploadEndpoint;
        }

        public String getUserImagesEditEndpoint() {
            return userImagesEditEndpoint;
        }

        public void setUserImagesEditEndpoint(String userImagesEditEndpoint) {
            this.userImagesEditEndpoint = userImagesEditEndpoint;
        }
    }
}
