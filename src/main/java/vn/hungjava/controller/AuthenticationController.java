package vn.hungjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hungjava.controller.request.SignInRequest;
import vn.hungjava.controller.response.TokenResponse;
import vn.hungjava.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Access token", description = "get access token an refresh token by username and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(HttpServletRequest httpRequest, @RequestBody SignInRequest request) {
        log.info("Access token request");
        System.out.println("Controller username = " + request.getUsername());
        System.out.println("Controller password = " + request.getPassword());
        System.out.println("Content-Type = " + httpRequest.getContentType());


        //return TokenResponse.builder().accessToken("DUMMY-ACCESS-TOKEN").refreshToken("DUMMY-REFRESH-TOKEN").build();
        return authenticationService.getAccessToken(request);
    }

    @Operation(summary = "Refresh token", description = "get new access token by refreshToken")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Refresh token request");
        return TokenResponse.builder().accessToken("DUMMY-NEW-ACCESS-TOKEN").refreshToken("DUMMY-REFRESH-TOKEN").build();

    }
}
