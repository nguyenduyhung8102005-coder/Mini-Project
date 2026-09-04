package vn.hungjava.service.impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.hungjava.common.UserStatus;
import vn.hungjava.controller.request.UserCreationRequest;
import vn.hungjava.controller.request.UserPasswordRequest;
import vn.hungjava.controller.request.UserUpdateRequest;
import vn.hungjava.controller.response.UserPageResponse;
import vn.hungjava.controller.response.UserResponse;
import vn.hungjava.exception.ResouceNotFoundException;
import vn.hungjava.model.UserEntity;
import vn.hungjava.repository.UserRepository;
import vn.hungjava.service.UserService;
import vn.hungjava.exception.InvalidDataException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
@Tag(name = "USER-SERVICE")
public class UserServiceImpl implements UserService {
    private  final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public UserPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all users");
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if(StringUtils.hasLength(sort)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()){
               String columName =  matcher.group(1);
               if(matcher.group(3).equalsIgnoreCase("esc")){
                   order =  new Sort.Order(Sort.Direction.ASC, columName);
               } else {
                   order =  new Sort.Order(Sort.Direction.DESC, columName);
               }
            }
        }

        int pageNo = 0;
        if(page > 0){
            pageNo = page - 1;
        }
        //phan trang
        Pageable pageAble = PageRequest.of(pageNo, size, Sort.by(order));
        //Tim kiem
        Page<UserEntity> entityPage = null;
        if(StringUtils.hasLength(keyword)){
            keyword = "%"+ keyword.toLowerCase() + "%";
            entityPage = userRepository.searchByKeyword(keyword, pageAble);
        } else {
            entityPage = userRepository.findAll(pageAble);
        }
        UserPageResponse response = getUserPageResponse(pageNo, size, entityPage);
        return response;

    }

    private static @NonNull UserPageResponse getUserPageResponse(int page, int size, Page<UserEntity> userEntities) {
        log.info("Convert userEntity");
        List<UserResponse> userList = userEntities.stream().map(entity -> UserResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .gender(entity.getGender())
                .birthDay(entity.getBirthDay())
                .userName(entity.getUsername())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .build()
        ).toList();

        UserPageResponse response = new UserPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(userEntities.getTotalElements());
        response.setTotalPages(userEntities.getTotalPages());
        response.setUsers(userList);
        return response;
    }

    @Override
    public UserResponse findById(long id) {
        log.info("Get user by id");
        UserEntity userEntity = getUser(id);

        return UserResponse.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .birthDay(userEntity.getBirthDay())
                .userName(userEntity.getUsername())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build();
    }

    @Override
    public UserResponse findByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse findByEmail(String email) {
        return null;
    }

    @Override
    public long save(UserCreationRequest req) {
        log.info("Saving user {}", req);

        UserEntity userByEmail = userRepository.findByEmail(req.getEmail());
        if(userByEmail != null){
            throw new InvalidDataException("User already exists");
        }

        UserEntity user = new  UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());
        user.setBirthDay(req.getBirthDay());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUserName());
        user.setUserType(req.getUserType());
        user.setStatus(UserStatus.NONE);
        userRepository.save(user);
        return 0;
    }

    @Override
    public void update(UserUpdateRequest user) {
        log.info("Updating user {}", user);
        UserEntity userUpdate = getCurentUser();
        userUpdate.setFirstName(user.getFirstName());
        userUpdate.setLastName(user.getLastName());
        userUpdate.setGender(user.getGender());
        userUpdate.setBirthDay(user.getBirthDay());
        userUpdate.setUsername(user.getUserName());
        userUpdate.setEmail(user.getEmail());
        userUpdate.setPhone(user.getPhone());

        userRepository.save(userUpdate);
        log.info("Updated user {}", user);

    }

    @Override
    public void changePassword(UserPasswordRequest request) {
        UserEntity currentUser =  getCurentUser();
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new InvalidDataException(
                    "Password and confirm password do not match"
            );
        }

        if (!encoder.matches(
                request.getCurrentPassword(),
                currentUser.getPassword()
        )) {
            throw new BadCredentialsException(
                    "Current password is incorrect"
            );
        }

        currentUser.setPassword(
                encoder.encode(request.getNewPassword())
        );
        currentUser.setTokenVersion(
                currentUser.getTokenVersion() + 1
        );

        userRepository.save(currentUser);

    }

    @Override
    public void deleted(long id) {
        UserEntity userDeleted = getUser(id);
        userDeleted.setStatus(UserStatus.INACTIVE);
        userRepository.save(userDeleted);
    }

    private UserEntity getUser(long id){
        return userRepository.findById(id).orElseThrow(() -> new ResouceNotFoundException("User not found"));
    }

    private UserEntity getCurentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new InvalidDataException(
                    "User is not authenticated"
            );
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    @Override
    public UserResponse findCurrentUser() {
        log.info("Get current user");

        return toUserResponse(
                getCurrentUser()
        );
    }

    private UserEntity getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new InvalidDataException(
                    "User is not authenticated"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal
                instanceof UserEntity currentUser)) {

            throw new InvalidDataException(
                    "Authenticated user is invalid"
            );
        }

        return currentUser;
    }

    private UserResponse toUserResponse(
            UserEntity userEntity
    ) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .birthDay(userEntity.getBirthDay())
                .userName(userEntity.getUsername())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build();
    }
}
