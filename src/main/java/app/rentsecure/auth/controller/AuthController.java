package app.rentsecure.auth.controller;

import app.rentsecure.auth.dto.*;
import app.rentsecure.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok("Registro exitoso. Revisa tu correo.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verificado. Ya puedes iniciar sesión.");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
