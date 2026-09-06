package vn.hungjava.service;

import vn.hungjava.controller.request.RegisterRequest;
import vn.hungjava.controller.request.SignInRequest;
import vn.hungjava.controller.response.TokenResponse;

public interface AuthenticationService {
    long register(RegisterRequest request);
    TokenResponse getAccessToken(SignInRequest request);
    TokenResponse getRefreshToken(String request);
}
