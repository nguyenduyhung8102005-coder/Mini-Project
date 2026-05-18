package vn.hungjava.controller.response;

import lombok.*;
import vn.hungjava.common.Gender;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date birthDay;
    private String userName;
    private String email;
    private String phone;
}
