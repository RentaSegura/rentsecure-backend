package app.rentsecure.config;

import app.rentsecure.auth.filter.JwtAuthenticationFilter;
import app.rentsecure.auth.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtFilter) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/actuator/health", "/health").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Parámetros recomendados por OWASP para Argon2id
        int saltLength = 16; // Longitud del salt en bytes
        int hashLength = 32; // Longitud del hash en bytes
        int parallelism = 1; // Número de hilos
        int memory = 65536; // Memoria en KiB (64 MB)
        int iterations = 2; // Número de iteraciones

        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder encoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(encoder);
        return builder.build();
    }
}
