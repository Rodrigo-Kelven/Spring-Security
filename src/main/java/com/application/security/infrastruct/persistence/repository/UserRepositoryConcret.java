package com.application.security.infrastruct.persistence.repository;

import com.application.security.infrastruct.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryConcret extends JpaRepository<UserEntity, Long> {

    Optional<UserDetails> findUserByEmail(String email);

    boolean existsByEmail(String email);
}
