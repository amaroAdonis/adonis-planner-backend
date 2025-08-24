package adonis.planner.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long expMs;
    private final long refExpMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String s,
                            @Value("${jwt.expirationMinutes}") long m,
                            @Value("${jwt.refreshExpirationDays}") long d) {
        this.key = Keys.hmacShaKeyFor(s.getBytes());
        this.expMs = m * 60_000L;
        this.refExpMs = d * 24 * 60 * 60_000L;
    }

    public String access(String sub) {
        Date n = new Date();
        return Jwts.builder().setSubject(sub).setIssuedAt(n)
                .setExpiration(new Date(n.getTime() + expMs)).signWith(key).compact();
    }

    public String refresh(String sub) {
        Date n = new Date();
        return Jwts.builder().setSubject(sub).setIssuedAt(n)
                .setExpiration(new Date(n.getTime() + refExpMs)).signWith(key).compact();
    }

    public String subject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public void validate(String token) {
        // apenas parse; se inválido/expirado, vai lançar exceção do JJWT
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
