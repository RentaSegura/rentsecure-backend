package app.rentsecure.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey key;
    private final long expSeconds;              // desde application.properties

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expSeconds = expSeconds;
    }

    /* ---------- Generación ---------- */

    public String generateToken(String subject, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                   .subject(subject)
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(now.plusSeconds(expSeconds)))
                   .claims(Map.of("roles", roles))
                   .signWith(key)
                   .compact();
    }

    /* ---------- Extracción / validación ---------- */

    /** Intenta parsear y valida firma + expiración. */
    public Optional<Jws<Claims>> parse(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                                  .verifyWith(key)
                                  .build()
                                  .parseSignedClaims(token);
            return Optional.of(jws);
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT inválido: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public boolean isTokenValid(String token) {
        return parse(token).isPresent();
    }

    public String extractUsername(String token) {
        return parse(token)
                .map(jws -> jws.getPayload().getSubject())
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return parse(token)
                .map(jws -> (List<String>) jws.getPayload().get("roles", List.class))
                .orElse(List.of());
    }
}
