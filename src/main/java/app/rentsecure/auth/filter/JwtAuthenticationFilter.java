package app.rentsecure.auth.filter;

import app.rentsecure.auth.service.impl.CustomUserDetailsService;
import app.rentsecure.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                if (jwtUtil.isTokenValid(token)) {
                    String email = jwtUtil.extractUsername(token);          // normalmente el “sub”
                    List<String> roles = jwtUtil.extractRoles(token);       // claim “roles” (array)

                    // Cargamos detalles del usuario para mantener compatibilidad con Spring Security
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // Mapeamos los roles (“LANDLORD”, “TENANT”, etc.) a autoridades Spring
                    var authorities = roles.stream()
                                           .map(SimpleGrantedAuthority::new)
                                           .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ex) {
                log.warn("JWT inválido o expirado: {}", ex.getMessage());
                // No lanzamos excepción → la petición seguirá sin autenticación (se bloqueará más adelante)
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Evita filtrar rutas públicas para ahorrar ciclos.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth") || path.startsWith("/actuator/health");
    }
}
