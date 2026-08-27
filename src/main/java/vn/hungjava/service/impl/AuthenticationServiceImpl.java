package vn.hungjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hungjava.common.TokenType;
import vn.hungjava.controller.request.SignInRequest;
import vn.hungjava.controller.response.TokenResponse;
import vn.hungjava.exception.ResouceNotFoundException;
import vn.hungjava.repository.UserRepository;
import vn.hungjava.service.AuthenticationService;
import vn.hungjava.service.JwtService;
import vn.hungjava.service.UserServiceDetail;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserServiceDetail userServiceDetail;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("get access token");
        List<String> authorities = new ArrayList<>();
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            log.info("isAuthenticated = {}", authentication.isAuthenticated());
            log.info("Authorities: {}", authentication.getAuthorities().toString());

            authorities.add(authentication.getAuthorities().toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.info("Authentication failed");
            throw new AccessDeniedException(e.getMessage());
        }

//        var user = userRepository.findByUsername(request.getUsername());
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
        String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(request.getUsername(), authorities);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        String username = jwtService.extractUsername(
                refreshToken,
                TokenType.REFRESH_TOKEN
        );

        UserDetails userDetails =
                userServiceDetail.loadUserByUsername(username);

        List<String> authorities =
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        String newAccessToken =
                jwtService.generateAccessToken(
                        username,
                        authorities
                );

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
