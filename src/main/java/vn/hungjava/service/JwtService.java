package vn.hungjava.service;

import org.springframework.security.core.GrantedAuthority;
import vn.hungjava.common.TokenType;

import java.util.Collection;
import java.util.List;

public interface JwtService {
    String generateAccessToken(Long userId, long tokenVersion ,List<String> authorities);
    String generateRefreshToken(Long userId, long tokenVersion, List<String> authorities);
    Long extractUserId(String token, TokenType type);
    long extractTokenVersion(String token, TokenType type);
}
