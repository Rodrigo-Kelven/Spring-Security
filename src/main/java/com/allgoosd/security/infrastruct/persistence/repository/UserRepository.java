package com.allgoosd.security.infrastruct.persistence.repository;

import com.allgoosd.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserDetails> findUserByEmail(String email);

    boolean existsByEmail(String email);
}
