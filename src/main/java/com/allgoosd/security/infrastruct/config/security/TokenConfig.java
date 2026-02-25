package com.allgoosd.security.infrastruct.config.security;

import com.allgoosd.security.domain.User;
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

    public String generateToken(User user){
        return JWT.create()
                .withClaim("userid", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }
}