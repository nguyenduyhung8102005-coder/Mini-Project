package vn.hungjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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
import org.springframework.transaction.annotation.Transactional;
import vn.hungjava.common.TokenType;
import vn.hungjava.common.UserStatus;
import vn.hungjava.common.UserType;
import vn.hungjava.controller.request.RegisterRequest;
import vn.hungjava.controller.request.SignInRequest;
import vn.hungjava.controller.response.TokenResponse;
import vn.hungjava.exception.InvalidDataException;
import vn.hungjava.exception.ResouceNotFoundException;
import vn.hungjava.model.Role;
import vn.hungjava.model.UserEntity;
import vn.hungjava.model.UserHasRole;
import vn.hungjava.repository.RoleRepository;
import vn.hungjava.repository.UserRepository;
import vn.hungjava.service.AuthenticationService;
import vn.hungjava.service.JwtService;
import vn.hungjava.service.UserServiceDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final UserServiceDetail userServiceDetail;
    private final RoleRepository  roleRepository;

    @Override
    @Transactional
    public long register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException(
                    "password and confirmPassword do not match"
            );
        }

        if (userRepository.existsByUsername(username)) {
            throw new InvalidDataException("Username already exists");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new InvalidDataException("Email already exists");
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException(
                        "Default role ROLE_USER is not configured"
                ));

        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setGender(request.getGender());
        user.setBirthDay(request.getBirthDay());
        user.setEmail(email);
        user.setPhone(normalizeOptional(request.getPhone()));
        user.setUsername(username);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setUserType(UserType.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setTokenVersion(0L);

        UserHasRole userRole = UserHasRole.builder()
                .user(user)
                .role(defaultRole)
                .build();
        user.getRoles().add(userRole);

        UserEntity savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("get access token");
        List<String> authorities = new ArrayList<>();
        Long userId;
        long tokenVersion;
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserEntity authenticatedUser =
                    (UserEntity) authentication.getPrincipal();
            userId = authenticatedUser.getId();
            tokenVersion = authenticatedUser.getTokenVersion();

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
        String accessToken = jwtService.generateAccessToken(userId, tokenVersion, authorities);
        String refreshToken = jwtService.generateRefreshToken(userId, tokenVersion, authorities);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        Long userId = jwtService.extractUserId(
                refreshToken,
                TokenType.REFRESH_TOKEN
        );

        long tokenVersion = jwtService.extractTokenVersion(
                refreshToken,
                TokenType.REFRESH_TOKEN
        );
        UserEntity user =
                (UserEntity)
                        userServiceDetail.loadUserById(userId);

        if (tokenVersion != user.getTokenVersion()) {
            throw new BadCredentialsException(
                    "Token has been revoked"
            );
        }

        UserDetails userDetails =
                userServiceDetail.loadUserById(userId);

        if (!userDetails.isEnabled()) {
            log.warn(
                    "Rejecting refresh token because account is inactive: {}",
                    userId
            );
            throw new DisabledException("Account is inactive");
        }

        List<String> authorities =
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        String newAccessToken =
                jwtService.generateAccessToken(
                        userId,
                        tokenVersion,
                        authorities
                );

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
