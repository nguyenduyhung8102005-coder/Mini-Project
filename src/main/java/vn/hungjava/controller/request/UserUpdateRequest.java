package vn.hungjava.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.hungjava.common.Gender;

import java.util.Date;

@Getter
@ToString
public class UserUpdateRequest {
    @NotNull(message = "firstName must be not null")
    private String firstName;
    @NotNull(message = "lastName must be not null")
    private String lastName;
    private Gender gender;
    private Date birthDay;
    @NotBlank(message = "UserName must be not blank")
    private String userName;
    @Email(message = "Email is invalid")
    private String email;
    private String phone;
}
