package com.transport.urbain.service;

import com.transport.urbain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of JWT token service.
 * 
 * <p>Handles JWT token generation, validation, and claim extraction.
 * Uses JJWT library for token operations with HMAC-SHA256 signing.
 * 
 * <p>Token structure:
 * <ul>
 *   <li>Access tokens: Include userId, email, and roles</li>
 *   <li>Refresh tokens: Include userId and type indicator</li>
 *   <li>Expiration times configured via application properties</li>
 * </ul>
 * 
 * <p>Security features:
 * <ul>
 *   <li>HMAC-SHA256 signing algorithm</li>
 *   <li>Configurable expiration times</li>
 *   <li>Claim validation and extraction</li>
 *   <li>Token expiration checking</li>
 * </ul>
 * 
 * <p>Configuration:
 * <ul>
 *   <li>jwt.secret - Secret key for signing</li>
 *   <li>jwt.expiration - Access token expiration (milliseconds)</li>
 *   <li>jwt.refresh-expiration - Refresh token expiration (default: 7 days)</li>
 * </ul>
 * 
 * @see com.transport.urbain.service.JwtService
 * @see io.jsonwebtoken.Jwts
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private Long refreshExpiration;

    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));

        return createToken(claims, user.getEmail(), jwtExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("type", "refresh");

        return createToken(claims, user.getEmail(), refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Long getExpirationTime() {
        return jwtExpiration;
    }
}
