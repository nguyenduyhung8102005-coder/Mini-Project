package vn.hungjava.service;

import vn.hungjava.controller.request.UserCreationRequest;
import vn.hungjava.controller.request.UserPasswordRequest;
import vn.hungjava.controller.request.UserUpdateRequest;
import vn.hungjava.controller.response.UserPageResponse;
import vn.hungjava.controller.response.UserResponse;

import java.util.List;

public interface UserService {
    UserPageResponse findAll(String keyword, String sort, int page, int size);
    UserResponse findById(long id);
    UserResponse findByUsername(String username);
    UserResponse findByEmail(String email);
    long save(UserCreationRequest req);
    void update(UserUpdateRequest req);
    void changePassword(UserPasswordRequest user);
    void deleted(long id);
    UserResponse findCurrentUser();
}
