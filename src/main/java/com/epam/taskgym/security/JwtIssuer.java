package com.epam.taskgym.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties jwtProperties;

    public String issue(Long userid, String username, List<String> roles) {
        return JWT.create()
                .withSubject(String.valueOf(userid))
                .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS)))
                .withClaim("u", username)
                .withClaim("a",roles)
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }
}
