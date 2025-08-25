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

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/auth/", "/api/v1/auth/", "/v3/api-docs/", "/swagger-ui/", "/actuator/health"
    );

    private static final Set<String> PUBLIC_EQUALS = Set.of(
            "/", "/actuator/health"
    );

    private boolean isPublic(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (PUBLIC_EQUALS.contains(uri)) return true;
        for (String p : PUBLIC_PREFIXES) {
            if (uri.startsWith(p)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        String h = req.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("[JWT] {} {} public? {} hasAuth? {}", req.getMethod(), uri, isPublic(req), (h != null && h.startsWith("Bearer ")));

        if (isPublic(req)) {
            fc.doFilter(req, res);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.info("[JWT] Já autenticado");
            fc.doFilter(req, res);
            return;
        }

        if (h == null || !h.startsWith("Bearer ")) {
            log.warn("[JWT] Sem header Authorization Bearer");
            fc.doFilter(req, res);
            return;
        }

        String token = h.substring(7);
        try {
            jwt.validate(token);
            String username = jwt.subject(token);
            log.info("[JWT] token ok; subject={}", username);

            UserDetails ud = uds.loadUserByUsername(username);
            var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("[JWT] SecurityContext setado");
        } catch (Exception ex) {
            log.warn("[JWT] inválido: {}", ex.toString());
        }

        fc.doFilter(req, res);
    }
}
