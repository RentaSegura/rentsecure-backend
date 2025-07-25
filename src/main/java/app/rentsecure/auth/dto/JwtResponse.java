package app.rentsecure.auth.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private Instant expiresAt;
    private List<String> roles;
}