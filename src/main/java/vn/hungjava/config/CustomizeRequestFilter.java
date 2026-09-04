package vn.hungjava.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.hungjava.common.TokenType;
import vn.hungjava.model.UserEntity;
import vn.hungjava.service.JwtService;
import vn.hungjava.service.UserServiceDetail;

import java.io.IOException;
import java.util.Date;

@Component
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomizeRequestFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserServiceDetail userServiceDetail;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
//            log.info("Bearer Auth Header: {}", authHeader.substring(0, 20));
            Long userId;
            long tokenVersion;
            try {
                userId = jwtService.extractUserId(authHeader, TokenType.ACCESS_TOKEN);
                tokenVersion = jwtService.extractTokenVersion(
                        authHeader,
                        TokenType.ACCESS_TOKEN
                );
                log.info("Username : {}", userId);
            } catch (AccessDeniedException e) {
                log.info("Access denied {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(errorResponse(e.getMessage()));
                return;
            }

            UserEntity userDetails =
                    (UserEntity) userServiceDetail.loadUserById(userId);

            if (tokenVersion != userDetails.getTokenVersion()) {
                log.warn(
                        "Rejecting revoked access token for user ID: {}",
                        userId
                );
                writeUnauthorizedResponse(response, "Token has been revoked");
                return;
            }

            if (!userDetails.isEnabled()) {
                log.warn(
                        "Rejecting access token because account is inactive: {}",
                        userId
                );

                writeUnauthorizedResponse(
                        response,
                        "Account is inactive"
                );

                return;
            }

            SecurityContext  securityContext = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken  authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(
            HttpServletResponse response,
            String message
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorResponse(message));
    }


    private String errorResponse(String message) {
        try {
            ErrorResponse error = new ErrorResponse();
            error.setTimestamp(new Date());
            error.setError("Unauthorized");
            error.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            error.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(error);
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
    }

    @Getter
    @Setter
    private class ErrorResponse{
        private Date  timestamp;
        private int status;
        private String error;
        private String message;
    }
}
