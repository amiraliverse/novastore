package com.app.novastore.security.signin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignInRequest {
    private String username;
    private String password;
}
