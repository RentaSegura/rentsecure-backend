package app.rentsecure.auth.service.impl;

import app.rentsecure.auth.dto.*;
import app.rentsecure.auth.entity.VerificationToken;
import app.rentsecure.auth.entity.User;
import app.rentsecure.auth.repository.UserRepository;
import app.rentsecure.auth.repository.VerificationTokenRepository;
import app.rentsecure.auth.service.RoleResolver;
import app.rentsecure.auth.service.AuthService;
import app.rentsecure.auth.service.EmailService;
import app.rentsecure.auth.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final VerificationTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final RoleResolver roleResolver;

    @Value("${environment.host}")
    private String host;
    @Value("${environment}")
    private String env;
    @Value("${jwt.expiration}")
    private long jwtExpirationSec;

    @Override
    @Transactional
    public void register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent())
            throw new IllegalStateException("Email ya registrado");

        String hash = encoder.encode(req.getPassword());
        User user = userRepo.save(User.newUser(req.getEmail(), hash));

        VerificationToken token = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plusSeconds(24 * 3600))
                .build();
        tokenRepo.save(token);

        // Construimos el link de verificación según si estamos en localhost o en producción
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.newInstance()
                .scheme(env.equals("localhost") ? "http" : "https")
                .host(env.equals("localhost") ? "localhost" : host)
                .path("/api/auth/verify")
                .queryParam("token", token.getToken());

        if (env.equals("localhost")) {
            urlBuilder.port(8080);
        }

        String link = urlBuilder.build().toUriString();
        emailService.sendVerificationEmail(user.getEmail(), link);
    }

    @Override
    @Transactional
    public void verifyEmail(String tokenStr) {
        VerificationToken token = tokenRepo.findByToken(tokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (token.getExpiresAt().isBefore(Instant.now()))
            throw new IllegalStateException("Token expirado");

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepo.save(user);

        tokenRepo.delete(token);
    }

    @Override
    public JwtResponse login(LoginRequest req) {
        Authentication auth;
        try {
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        } catch (DisabledException ex) {
            throw new IllegalStateException("Cuenta no verificada. Revisa tu correo.");
        } catch (Exception ex) {
            throw new IllegalStateException("Credenciales inválidas");
        }

        User user = (User) auth.getPrincipal();

        List<String> roles = roleResolver.resolveRoles(user.getId());
        String jwt = jwtUtil.generateToken(user.getEmail(), roles);
        Instant exp = Instant.now().plusSeconds(jwtExpirationSec);

        return new JwtResponse(jwt, exp, roles);
    }
}
