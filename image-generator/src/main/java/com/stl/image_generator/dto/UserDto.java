package com.stl.image_generator.dto;

import com.stl.image_generator.api.entities.User;
import com.stl.image_generator.api.entities.UserRole;

import java.time.Instant;
import java.util.UUID;

public class UserDto extends BasicDto {

    private UUID uuid;
    private String name;
    private String icon;
    private boolean isActive;
    private UserRole userRole;

    // Default constructor
    public UserDto() {
    }

    // Constructor that takes a User entity as input
    public UserDto(User user) {
        if (user != null) {
            this.uuid = user.getUuid();
            this.name = user.getName();
            this.icon = user.getIcon();
            this.isActive = user.isActive();
            this.userRole = user.getUserRole();
            setCreatedTime(user.getCreatedTime());
            setUpdateTime(user.getUpdateTime());
            setCreatedBy(user.getCreatedBy());
            setUpdatedBy(user.getUpdatedBy());
        }
    }

    // Getters and Setters for all fields
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    // Optional: Add a builder pattern for UserDto for easy object creation
    public static class Builder {
        private UUID uuid;
        private String name;
        private String icon;
        private boolean isActive;
        private UserRole userRole;
        private Instant createdTime;
        private Instant updateTime;
        private UUID createdBy;
        private UUID updatedBy;

        public Builder withUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder withIsActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder withUserRole(UserRole userRole) {
            this.userRole = userRole;
            return this;
        }

        public Builder withCreatedTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder withUpdateTime(Instant updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder withCreatedBy(UUID createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withUpdatedBy(UUID updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public UserDto build() {
            UserDto userDto = new UserDto();
            userDto.setUuid(this.uuid);
            userDto.setName(this.name);
            userDto.setIcon(this.icon);
            userDto.setActive(this.isActive);
            userDto.setUserRole(this.userRole);
            userDto.setCreatedTime(this.createdTime);
            userDto.setUpdateTime(this.updateTime);
            userDto.setCreatedBy(this.createdBy);
            userDto.setUpdatedBy(this.updatedBy);
            return userDto;
        }
    }
}
