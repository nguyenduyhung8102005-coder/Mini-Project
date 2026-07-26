package vn.hungjava.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import vn.hungjava.common.TokenType;
import vn.hungjava.exception.InvalidDataException;
import vn.hungjava.service.JwtService;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static vn.hungjava.common.TokenType.ACCESS_TOKEN;
import static vn.hungjava.common.TokenType.REFRESH_TOKEN;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.expiryMinutes}")
    private long expiryMinutes;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessKey}")
    private String accessKey;

    @Value("${jwt.refreshKey}")
    private String refreshKey;


    @Override
    public String generateAccessToken(String username, List<String> authorities) {
        log.info("generate access token for username {} with authorities {}", username, authorities );
        Map<String, Object> claims =  new HashMap<>();

        claims.put("role", authorities);
        return generateToken(claims, username);
    }

    @Override
    public String generateRefreshToken(String username, List<String> authorities) {
        log.info("generate refresh token for username {} with authorities {}", username, authorities );
        Map<String, Object> claims =  new HashMap<>();

        claims.put("role", authorities);
        return generateRefreshToken(claims, username);
    }

    @Override
    public String extractUsername(String token, TokenType type) {
        log.info("extract username from token {}", token );
        return extractClaims(type, token, Claims::getSubject);
    }

    private <T> T extractClaims(TokenType type, String token, Function<Claims, T> claimsExtractor) {
        final Claims claims = extractAllClaim(token, type);
        return claimsExtractor.apply(claims);
    }

    private Claims extractAllClaim(String token, TokenType type){
        try {
            return Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token).getBody();
        } catch (SignatureException | ExpiredJwtException e) {
            throw new AccessDeniedException("Access denied, error: " + e.getMessage());
        }
    }

    private String generateToken(Map<String, Object> claims, String username){
        log.info("generate token for user {}", username );
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
                .signWith(getKey(ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, String username){
        log.info("generate refresh token for user {}", username );
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expiryDay))
                .signWith(getKey(REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type) {
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> {throw new InvalidDataException("Invalid token type");}
        }
    }
}
