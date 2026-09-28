package com.app.novastore.security.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body of the refresh exchange. Optional on purpose: the cookie written at login is the
 * primary carrier, and this is the alternative for clients that hold their own tokens.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    private String refreshToken;
}
