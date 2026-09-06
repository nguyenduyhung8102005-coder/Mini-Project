package vn.hungjava.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hungjava.common.Gender;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "firstName must not be blank")
    @Size(max = 100, message = "firstName must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    @Size(max = 100, message = "lastName must not exceed 100 characters")
    private String lastName;

    private Gender gender;

    @Past(message = "birthDay must be in the past")
    private Date birthDay;

    @NotBlank(message = "username must not be blank")
    @Size(min = 3, max = 50, message = "username must contain 3 to 50 characters")
    private String username;

    @NotBlank(message = "email must not be blank")
    @Email(message = "email is invalid")
    @Size(max = 255, message = "email must not exceed 255 characters")
    private String email;

    @Size(max = 15, message = "phone must not exceed 15 characters")
    private String phone;

    @NotBlank(message = "password must not be blank")
    @Size(min = 8, max = 72, message = "password must contain 8 to 72 characters")
    private String password;

    @NotBlank(message = "confirmPassword must not be blank")
    private String confirmPassword;

    @JsonIgnore
    @AssertTrue(message = "password and confirmPassword do not match")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
