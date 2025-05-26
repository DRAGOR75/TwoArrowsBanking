package dragor.com.webapp.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public SecretKey generateKey() {
        byte[] secretKeyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(System.currentTimeMillis() + 86400000); // 24 hours
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(generateKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseClaimsJws(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
       return new Date().before(extractExpiration(token));
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }
}
