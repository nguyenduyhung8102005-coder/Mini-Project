package vn.hungjava.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserPasswordRequest {
    @NotBlank(message = "currentPassword must be not blank")
    private String currentPassword;
    @NotBlank(message = "newPassword must be not blank")
    private String newPassword;
    @NotBlank(message = "confirmPassword must be not blank")
    private String confirmPassword;
}
