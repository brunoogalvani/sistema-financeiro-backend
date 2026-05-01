package com.bruno.sistemafinanceiro.configs;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bruno.sistemafinanceiro.entities.User;
import com.bruno.sistemafinanceiro.entities.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenConfig {

    @Value("${jwt_secret}")
    private String secret;

    public String generateToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userid", user.getId().toString())
                .withClaim("name", user.getName())
                .withClaim("role", user.getRole().toString())
                .withSubject(user.getUsername())
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decode = JWT.require(algorithm)
                    .build().verify(token);

            return Optional.of(JWTUserData.builder()
                    .userId(UUID.fromString(decode.getClaim("userid").asString()))
                    .username(decode.getSubject())
                    .role(UserRole.valueOf(decode.getClaim("role").asString()))
                    .build());
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }
}
