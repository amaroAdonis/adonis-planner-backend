package adonis.planner.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwt;
    private final CustomUserDetailsService uds;

    // paths públicos (ajuste conforme seus controllers)
    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/auth/", "/api/v1/auth/", "/v3/api-docs/", "/swagger-ui/", "/actuator/health", "/"
    );

    private boolean isPublic(HttpServletRequest req) {
        String uri = req.getRequestURI();
        // começa com qualquer prefixo público
        for (String p : PUBLIC_PREFIXES) {
            if (p.endsWith("/") ? uri.startsWith(p) : uri.equals(p)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {

        // 1) pule rotas públicas
        if (isPublic(req)) {
            fc.doFilter(req, res);
            return;
        }

        // 2) se já está autenticado, segue
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            fc.doFilter(req, res);
            return;
        }

        // 3) pegue o header
        String h = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (h == null || !h.startsWith("Bearer ")) {
            // sem token -> deixe o Spring decidir (vai dar 401 em rotas protegidas)
            fc.doFilter(req, res);
            return;
        }

        String token = h.substring(7);
        try {
            // opcional: verificação explícita (expiração/assinatura)
            jwt.validate(token); // implemente no JwtTokenProvider (lançar exceção se inválido)

            String username = jwt.subject(token);
            UserDetails ud = uds.loadUserByUsername(username);

            var auth = new UsernamePasswordAuthenticationToken(
                    ud, null, ud.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception ex) {
            // não devolva 403 aqui; apenas logue e siga
            log.debug("JWT inválido: {}", ex.getMessage());
        }

        fc.doFilter(req, res);
    }
}
