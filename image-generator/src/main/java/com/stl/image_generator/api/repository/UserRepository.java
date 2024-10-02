package com.stl.image_generator.api.repository;

import com.stl.image_generator.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByName(String name);

    // Find all users where isActive is true
    List<User> findByIsActiveTrue();

    User findByUuid(UUID uuid);

}
