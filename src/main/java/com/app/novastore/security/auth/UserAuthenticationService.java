package com.app.novastore.security.auth;

import com.app.novastore.annotations.cookie.StoreTokenInCookie;
import com.app.novastore.annotations.otp.PreventDuplicateOtp;
import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.constants.NovastoreConstants;
import com.app.novastore.errors.AppErrorCodes;
import com.app.novastore.exception.ApplicationException;
import com.app.novastore.exception.IncorrectOtpCodeException;
import com.app.novastore.exception.RecordNotFoundException;
import com.app.novastore.exception.UserIsDisabledException;
import com.app.novastore.security.SecurityUtils;
import com.app.novastore.security.otp.GenericOtp;
import com.app.novastore.security.otp.GenericOtpService;
import com.app.novastore.security.otp.payload.OtpAccessKeyResponse;
import com.app.novastore.security.otp.payload.OtpGenerateRequest;
import com.app.novastore.security.otp.payload.OtpValidateRequest;
import com.app.novastore.security.otp.signin.SignInOtpService;
import com.app.novastore.security.session.SessionService;
import com.app.novastore.security.signin.UserSignInRequest;
import com.app.novastore.security.token.GenericAuthToken;
import com.app.novastore.security.token.TokenProvider;
import com.app.novastore.security.token.refresh.RefreshTokenService;
import com.app.novastore.security.user.PlatformUser;
import com.app.novastore.security.user.UserService;
import com.app.novastore.security.user.type.UserType;
import com.app.novastore.util.CookieUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;


@Service
@Transactional(readOnly = true)
public class UserAuthenticationService {

    private final GenericOtpService genericOtpService;
    private final TokenProvider tokenProvider;
    private final AuthenticationProvider authenticationProvider;
    private final UserService userService;
    private final NovastoreProperties properties;
    private final RefreshTokenService refreshTokenService;
//    private final SmsSender smsSender;
//    private final OtpMessageGenerator otpMessageGenerator;
    private final Environment environment;
    private final SessionService sessionService;

    @PreventDuplicateOtp
    public OtpAccessKeyResponse generateOtpForCustomer(OtpGenerateRequest otpRequest) {
        return generateOtp(otpRequest, UserType.CUSTOMER);
    }

    @Transactional
    @StoreTokenInCookie
    public GenericAuthToken validateOtpForCustomer(OtpValidateRequest request) {
        return validateOtp(request, UserType.CUSTOMER);
    }

    @PreventDuplicateOtp
    public OtpAccessKeyResponse generateOtp(OtpGenerateRequest otpRequest, UserType type) {
        PlatformUser platformUser = userService.findByMobileAndTypeAndDeletedIsFalse(otpRequest.getMobile(), type)
                .map(u -> {
                    validateFoundUser(u);
                    return u;
                })
                .orElse(new PlatformUser());

        GenericOtp otp = genericOtpService.generate(otpRequest.getMobile());
        sendOtpMessage(platformUser, otp);
        return new OtpAccessKeyResponse(otp.getAccessKey());
    }

    private void sendOtpMessage(PlatformUser platformUser, GenericOtp otp) {
        if (Arrays.asList(environment.getActiveProfiles()).contains(NovastoreConstants.SPRING_PROFILE_TEST))
            return;

//        Message<String> message = otpMessageGenerator.generate(user.getFullName(), otp.getId(), otp.getCode());
//        smsSender.send(message);
    }

    public GenericAuthToken validateOtp(OtpValidateRequest request, UserType type) {
        return genericOtpService.findById(request.getMobile())
                .filter(code -> code.getCode().contentEquals(request.getCode()) && code.getAccessKey().contentEquals(request.getAccessKey()))
                .map(code -> {
                    PlatformUser savedPlatformUser = userService.findByMobileAndTypeAndDeletedIsFalse(code.getIdentifier(), type)
                            .orElseGet(() -> {
                                final PlatformUser platformUser = PlatformUser.builder()
                                        .mobile(request.getMobile())
                                        .type(type)
                                        .build();

                                return userService.persistWithDefaultConfig(platformUser);
                            });
                    GenericAuthToken token = tokenProvider.createToken(savedPlatformUser);
                    sessionService.persist(sessionService.buildSession(token, savedPlatformUser));
                    return token;
                })
                .orElseThrow(IncorrectOtpCodeException::new);
    }

//    @StoreTokenInCookie because apisix header buffer cant hold long header and admin jwt which is saved in cookie is too long
    /**
     * Password sign-in for every user type, not just staff: a customer's username is their
     * mobile number (see {@code UserService#resolvePreferredUsername}), so the customer app
     * signs in with the same endpoint by passing the mobile as the username.
     */
    public GenericAuthToken signInByPassword(UserSignInRequest request) {
        return userService.findByUsernameAndDeletedIsFalse(request.getUsername())
                .map(user -> {
                    validateFoundUser(user);
                    GenericAuthToken token = tokenProvider.createToken(
                            (PlatformUser) authenticationProvider.authenticate(
                                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                            ).getPrincipal()
                    );
                    sessionService.persist(sessionService.buildSession(token, user));
                    return token;
                })
                .orElseThrow(RecordNotFoundException::new);
    }

    /**
     * Exchanges a refresh token for a fresh pair, rotating it: the presented token is deleted
     * before the new one is issued, so a leaked refresh token is single-use.
     */
    @StoreTokenInCookie
    public GenericAuthToken getTokenByRefreshToken(RefreshTokenRequest request) {
        String id = resolveRefreshToken(request);

        return refreshTokenService.findById(id)
                .map(refreshToken -> {
                    refreshTokenService.deleteById(id);
                    PlatformUser platformUser = userService.findActiveByIdAndDeletedIsFalse(refreshToken.getUserId());
                    return tokenProvider.createToken(platformUser);
                })
                .orElseThrow(RecordNotFoundException::new);
    }

    /**
     * Cookie first, body second - the same precedence the access token follows in
     * {@link com.app.novastore.util.JwtUtils#resolveJwt(String)}. The cookie is the main
     * auth carrier; the body serves clients that keep the token returned in the response.
     */
    private String resolveRefreshToken(RefreshTokenRequest request) {
        String fromCookie = CookieUtils.get(properties.getSecurity().getCookie().getRefreshTokenKey());
        if (StringUtils.hasText(fromCookie)) {
            return fromCookie;
        }

        String fromBody = request == null ? null : request.getRefreshToken();
        if (StringUtils.hasText(fromBody)) {
            return fromBody;
        }

        throw new ApplicationException(AppErrorCodes.MALFORMED_REQUEST);
    }

    /**
     * Ends the caller's session. The JWT stays cryptographically valid until it expires, so
     * revocation is what makes logout mean anything - see
     * {@link com.app.novastore.security.token.invalidation.TokenInvalidationService}.
     * <p>
     * Deliberately not {@code @Transactional}: every effect here is a Redis write or a
     * cookie header, none of which enrol in a JPA transaction, so the annotation would
     * promise an atomicity it cannot deliver. The steps are ordered so that each one is
     * safe on its own - a failure part-way through leaves the session more revoked, never
     * less.
     */
    public void logout() {
        SecurityUtils.getCurrentUserJwt().ifPresent(sessionService::revokeCurrentSession);

        NovastoreProperties.Security.Cookie cookie = properties.getSecurity().getCookie();
        CookieUtils.remove(cookie.getJwtTokenKey());
        CookieUtils.remove(cookie.getRefreshTokenKey());
    }

    private void validateFoundUser(PlatformUser platformUser) {
        if (!platformUser.getActive())
            throw new UserIsDisabledException();
    }

    public UserAuthenticationService(@Qualifier(SignInOtpService.BEAN_NAME) GenericOtpService genericOtpService,
//                                     @Qualifier(CarbonConstants.SMS_SENDER_BEAN_NAME) SmsSender smsSender,
                                     TokenProvider tokenProvider,
                                     AuthenticationProvider authenticationProvider,
                                     UserService userService,
                                     NovastoreProperties properties,
                                     RefreshTokenService refreshTokenService,
//                                     OtpMessageGenerator otpMessageGenerator,
                                     Environment environment,
                                     SessionService sessionService) {
        this.genericOtpService = genericOtpService;
        this.tokenProvider = tokenProvider;
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
        this.properties = properties;
        this.refreshTokenService = refreshTokenService;
//        this.smsSender = smsSender;
//        this.otpMessageGenerator = otpMessageGenerator;
        this.environment = environment;
        this.sessionService = sessionService;
    }
}
