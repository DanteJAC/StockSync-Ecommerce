package com.stockSync.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stockSync.backend.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findByOwnerId(Long ownerId);
}
