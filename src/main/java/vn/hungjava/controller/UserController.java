package vn.hungjava.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hungjava.controller.request.UserCreationRequest;
import vn.hungjava.controller.request.UserPasswordRequest;
import vn.hungjava.controller.request.UserUpdateRequest;
import vn.hungjava.controller.response.UserPageResponse;
import vn.hungjava.controller.response.UserResponse;
import vn.hungjava.service.UserService;
import vn.hungjava.service.impl.UserServiceImpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
@Validated
@Tag(name = "Test api")
public class UserController {
    private final UserService userService;

    @GetMapping("/list")
    public Map<String, Object> getListUser(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String sort,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size){
        log.info("Get list user");
        UserPageResponse users = userService.findAll(keyword, sort, page, size);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get list users");
        result.put("data", users);

        return  result;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getUser(@PathVariable("userId") @Min(1) long userId){
        log.info("Get user");
        UserResponse user = userService.findById(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Get user");
        result.put("data", user);

        return result;
    }

    @PostMapping("/add")
    public Map<String, Object> postUser(@RequestBody @Valid UserCreationRequest user){
        log.info("Post user");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Post user");
        result.put("data", userService.save(user));

        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> updateUser(@RequestBody @Valid UserUpdateRequest user){
        log.info("Update user");
        userService.update(user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        result.put("data", "");
        return result;
    }

    @PatchMapping("/update")
    public Map<String, Object> changePassword(@RequestBody @Valid UserPasswordRequest user){
        log.info("Change password");
        userService.changePassword(user);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "Change password successfully");
        result.put("data", "");
        return result;
    }

    @DeleteMapping("/delete/{userId}")
    public Map<String, Object> deleteUser(@PathVariable @Min(1) long userId){
        log.info("Delete user");
        userService.deleted(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Delete user successfully");
        result.put("data", "");
        return result;
    }

}
