package org.acme.awesomepizza.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private static final Duration EXPIRATION = Duration.ofHours(1);

    @Value("${application.jwt.secret}")
    private String secret;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION.toMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<String> validateToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        boolean valid = claims.getExpiration().after(new Date());
        return valid ? Optional.of(claims.getSubject()) : Optional.empty();
    }

    public Cookie generateCookie(String token) {
        Cookie cookie = new Cookie("awesome-pizza-jwt", token);
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) Duration.ofHours(1).toSeconds());
        return cookie;
    }
}
