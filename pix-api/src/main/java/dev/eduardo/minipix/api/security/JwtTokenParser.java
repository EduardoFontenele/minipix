package dev.eduardo.minipix.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenParser {

    @Value("${pix.security.jwt-secret}")
    private String jwtSecret;

    public String extractDocument(String authorizationHeader) {
        var token = authorizationHeader.replace("Bearer ", "");
        var key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
