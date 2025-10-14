package org.example.bank.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.bank.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.accessTokenExpirationMin}")
    private long validityInMin;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    //Создание JWT токена с ролями
    public String createToken(String username, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMin * 60_000); // минуты → миллисекунды

        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Получить имя пользователя из токена
    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    //Получить роли пользователя из токена
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object roles = getAllClaims(token).get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    // Проверка валидности токена
    public boolean validateToken(String token) {
        try {
            getAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
