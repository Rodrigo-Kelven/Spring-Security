package com.application.security.infrastruct.config.security;

import com.application.security.infrastruct.persistence.entity.UserEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
@Component
public class TokenConfig {

    private final String secret;
    private final Algorithm algorithm;

    public TokenConfig(@Value("${JWT_SECRET}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret cannot be null or empty");
        }
        this.secret = secret;
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(UserEntity userEntity){
        return JWT.create()
                .withClaim("userid", userEntity.getId())
                .withSubject(userEntity.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }
}