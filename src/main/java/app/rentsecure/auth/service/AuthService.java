package app.rentsecure.auth.service;

import app.rentsecure.auth.dto.*;

public interface AuthService {
    void register(RegisterRequest req);
    void verifyEmail(String token);
    JwtResponse login(LoginRequest req);
}
