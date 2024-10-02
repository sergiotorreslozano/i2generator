package com.stl.image_generator.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "APP_USER")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column
    private String name;

    @Column
    private String icon;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)  // Map Enum as String in DB
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;  // New field for user role

    // One User can have multiple Images
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Image> images;

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public User() {}

    // Private constructor to prevent direct instantiation
    private User(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.icon = builder.icon;
        this.isActive = builder.isActive;
        this.userRole = builder.userRole;  // Initialize the new userRole field
        this.images = builder.images;

        // Set inherited fields from BaseEntity
        setCreatedTime(builder.createdTime);
        setUpdateTime(builder.updateTime);
        setCreatedBy(builder.createdBy);
        setUpdatedBy(builder.updatedBy);
    }

    // Getters and setters for the new field
    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Getters for existing fields
    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public List<Image> getImages() {
        return images;
    }

    // Static inner Builder class
    public static class Builder {
        private UUID uuid;
        private String name;
        private String icon;
        private List<Image> images;
        private boolean isActive = true;
        private UserRole userRole;  // New field for userRole

        // Inherited fields from BaseEntity
        private Instant createdTime;
        private Instant updateTime;
        private UUID createdBy;
        private UUID updatedBy;

        public Builder() {}

        // Builder method for userRole
        public Builder withUserRole(UserRole userRole) {
            this.userRole = userRole;
            return this;
        }

        // Builder method for isActive
        public Builder withIsActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        // Builder method to set the UUID
        public Builder withUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        // Builder method to set the name
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        // Builder method to set the icon
        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder withImages(List<Image> images) {
            this.images = images;
            return this;
        }

        // Builder methods for inherited fields
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

        // Build method to create a User object
        public User build() {
            return new User(this);
        }
    }

    // Optional: Add a static builder() method to easily start building
    public static Builder builder() {
        return new Builder();
    }
}
