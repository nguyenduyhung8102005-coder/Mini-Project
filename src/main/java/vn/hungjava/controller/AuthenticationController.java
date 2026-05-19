package vn.hungjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hungjava.controller.request.SigInRequest;
import vn.hungjava.controller.response.TokenResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@Tag(name = "Authentication")
public class AuthenticationController {

    @Operation(summary = "Access token", description = "get access token an refresh token by username and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody SigInRequest request) {
        log.info("Access token request");
        return TokenResponse.builder().accessToken("DUMMY-ACCESS-TOKEN").refreshToken("DUMMY-REFRESH-TOKEN").build();

    }

    @Operation(summary = "Refresh token", description = "get new access token by refreshToken")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Refresh token request");
        return TokenResponse.builder().accessToken("DUMMY-NEW-ACCESS-TOKEN").refreshToken("DUMMY-REFRESH-TOKEN").build();

    }
}
