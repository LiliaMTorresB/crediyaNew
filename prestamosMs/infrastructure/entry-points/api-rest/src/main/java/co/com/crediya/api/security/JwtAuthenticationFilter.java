package co.com.crediya.api.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidatorService jwtValidatorService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtValidatorService.esTokenValido(token)) {
            Claims claims = jwtValidatorService.extraerClaims(token);
            String correo = claims.getSubject();
            String rol = claims.get("rol", String.class);
            String documento = claims.get("documento", String.class);

            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String authority = rol != null ? "ROLE_" + rol.toUpperCase() : "ROLE_SOLICITANTE";
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        correo,
                        token,
                        List.of(new SimpleGrantedAuthority(authority))
                );
                authToken.setDetails(Map.of(
                        "correo", correo,
                        "documento", documento != null ? documento : "",
                        "rol", rol != null ? rol : ""
                ));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Usuario autenticado en prestamosMs vía JWT: {} con rol {}", correo, authority);
            }
        } else {
            log.warn("Token JWT inválido o expirado en petición a {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}