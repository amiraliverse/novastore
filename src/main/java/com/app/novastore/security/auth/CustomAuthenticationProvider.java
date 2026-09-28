package com.app.novastore.security.auth;

import com.app.novastore.exception.RecordNotFoundException;
import com.app.novastore.exception.UserIsDisabledException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.debug("Authenticating user: {}, password: {}", username, password);

        return Optional.ofNullable(userDetailsService.loadUserByUsername(username))
                .map(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword()))
                        throw new BadCredentialsException("incorrect password");

                    if (!user.isEnabled())
                        throw new UserIsDisabledException("user is disabled");

                    return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
                })
                .orElseThrow(RecordNotFoundException::new);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
