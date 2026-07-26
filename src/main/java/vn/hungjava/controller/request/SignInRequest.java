package vn.hungjava.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest implements Serializable {
    private String username;
    private String password;
    private String platform;
    private String deviceToken;
    private String versionApp;
}
