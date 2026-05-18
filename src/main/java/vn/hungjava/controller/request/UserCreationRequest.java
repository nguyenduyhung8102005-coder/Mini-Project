package vn.hungjava.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import vn.hungjava.common.Gender;
import vn.hungjava.common.UserStatus;
import vn.hungjava.common.UserType;

import java.util.Date;

@Getter
public class UserCreationRequest {
    @NotNull(message = "firtName must be not null")
    private String firstName;
    @NotNull(message = "lastName must be not null")
    private String lastName;
    private Gender gender;
    private Date birthDay;
    @NotBlank(message = "UserName is not blank")
    private String userName;
    @Email(message = "email is invalid")
    private String email;
    private String phone;
    private UserType userType;
}
