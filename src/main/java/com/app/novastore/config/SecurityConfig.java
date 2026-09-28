package com.app.novastore.config;

import com.app.novastore.constants.NovastoreConstants;
import com.app.novastore.security.auth.CustomAuthenticationEntryPoint;
import com.app.novastore.security.token.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/auth/customer/otp/v1/generate").permitAll()
                        .requestMatchers("/api/users/auth/customer/otp/v1/validate").permitAll()
                        .requestMatchers("/api/users/auth/driver/otp/v1/generate").permitAll()
                        .requestMatchers("/api/users/auth/driver/otp/v1/validate").permitAll()
                        .requestMatchers("/api/users/auth/v1/sign-in").permitAll()
                        .requestMatchers("/api/users/auth/v1/token").permitAll()
                        // Probe endpoints: prod reports status only (show-details: never).
                        .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                        // Everything else under the management base-path is operator-only.
                        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAuthority(NovastoreConstants.ROOT_AUTHORITY)
                        .anyRequest().fullyAuthenticated()
                )
                .cors(Customizer.withDefaults())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        // JSON API plus Swagger: nothing here should ever be framed, load a
                        // subresource, or leak the request URL to a third party.
                        // 'self' rather than 'none' so the Swagger UI still loads where it is enabled.
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; frame-ancestors 'none'; base-uri 'none'"))
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
