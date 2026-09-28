package com.app.novastore.security.auth;

import com.app.novastore.security.otp.payload.OtpAccessKeyResponse;
import com.app.novastore.security.otp.payload.OtpGenerateRequest;
import com.app.novastore.security.otp.payload.OtpValidateRequest;
import com.app.novastore.security.signin.UserSignInRequest;
import com.app.novastore.security.token.GenericAuthToken;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users/auth")
public class UserAuthenticationResource {

    private final UserAuthenticationService authenticationService;

    @PostMapping("/otp/v1/generate")
    public ResponseEntity<OtpAccessKeyResponse> generateOtpForCustomer(@RequestBody @Valid OtpGenerateRequest request) {
        return new ResponseEntity<>(authenticationService.generateOtpForCustomer(request), HttpStatus.OK);
    }

    @PostMapping("/otp/v1/validate")
    public ResponseEntity<GenericAuthToken> validateOtpForCustomer(@RequestBody @Valid OtpValidateRequest request) {
        return new ResponseEntity<>(authenticationService.validateOtpForCustomer(request), HttpStatus.OK);
    }

    @PostMapping("/v1/sign-in")
    public ResponseEntity<GenericAuthToken> signInByPassword(@RequestBody @Valid UserSignInRequest request) {
        return new ResponseEntity<>(authenticationService.signInByPassword(request), HttpStatus.OK);
    }

    /**
     * Exchanges a refresh token for a new token pair. The token is taken from the refresh
     * cookie, or from the body when there is no cookie.
     * <p>
     * POST, never {@code GET /token/{id}}: a URL path is recorded verbatim by access logs,
     * proxies, browser history and {@code Referer} headers, and this is a 60-day credential.
     */
    @PostMapping("/v1/token")
    public ResponseEntity<GenericAuthToken> getTokenByRefreshToken(@RequestBody(required = false) @Valid RefreshTokenRequest request) {
        return new ResponseEntity<>(authenticationService.getTokenByRefreshToken(request), HttpStatus.OK);
    }

    /**
     * Revokes the caller's current session: the JWT is denied for the rest of its lifetime,
     * its refresh token is dropped, and the auth cookies are expired.
     */
    @PostMapping("/v1/logout")
    public ResponseEntity<Void> logout() {
        authenticationService.logout();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
