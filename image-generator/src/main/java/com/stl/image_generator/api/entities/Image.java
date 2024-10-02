package com.stl.image_generator.api.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "IMAGE")
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 10485760)
    private String b64json;

    @Column(length = 2024)
    private String prompt;

    @Column(length = 2024)
    private String modifiedPrompt;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_status")
    private ImageStatus imageStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "creation_type")
    private CreationType creationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Image() {
    }

    private Image(Builder builder) {
        this.id = builder.id;
        this.prompt = builder.prompt;
        this.b64json = builder.b64json;
        this.modifiedPrompt = builder.modifiedPrompt;
        this.imageStatus = builder.imageStatus;  // New field
        this.creationType = builder.creationType;  // Assign new creationType
        this.user = builder.user;

        setCreatedTime(builder.createdTime);
        setUpdateTime(builder.updateTime);
        setCreatedBy(builder.createdBy);
        setUpdatedBy(builder.updatedBy);
    }

    public Long getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getB64json() {
        return b64json;
    }

    public String getModifiedPrompt() {
        return modifiedPrompt;
    }

    public ImageStatus getImageStatus() {
        return imageStatus;
    }

    public CreationType getCreationType() {
        return creationType;
    }

    public User getUser() {
        return user;
    }

    // Static Builder class
    public static class Builder {
        private Long id;
        private String prompt;
        private String b64json;
        private String modifiedPrompt;
        private ImageStatus imageStatus;  // New field
        private User user;
        private CreationType creationType;
        private Instant createdTime;
        private Instant updateTime;
        private UUID createdBy;
        private UUID updatedBy;

        public Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withPrompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder withB64json(String b64json) {
            this.b64json = b64json;
            return this;
        }

        public Builder withModifiedPrompt(String modifiedPrompt) {
            this.modifiedPrompt = modifiedPrompt;
            return this;
        }

        public Builder withImageStatus(ImageStatus imageStatus) {  // New builder method
            this.imageStatus = imageStatus;
            return this;
        }

        public Builder withCreationType(CreationType creationType) {  // Add method to set creationType
            this.creationType = creationType;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
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

        public Image build() {
            return new Image(this);
        }
    }

    public Image cloneWithoutImage() {
        return new Builder()
                .withId(this.id)  // Include the ID if you want the cloned object to have the same ID
                .withPrompt(this.prompt)
                .withModifiedPrompt(this.modifiedPrompt)
                .withImageStatus(this.imageStatus)
                .withCreationType(this.creationType)  // Include creationType in the clone
                .withUser(this.user)  // Assuming a shallow copy of the User is sufficient; adjust if deep copy is needed
                .withCreatedTime(this.getCreatedTime())
                .withUpdateTime(this.getUpdateTime())
                .withCreatedBy(this.getCreatedBy())
                .withUpdatedBy(this.getUpdatedBy())
                .build();
    }
}
