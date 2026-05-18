package vn.hungjava.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserPageResponse extends PageResponseAbtract implements Serializable {
    private List<UserResponse> users;

}
