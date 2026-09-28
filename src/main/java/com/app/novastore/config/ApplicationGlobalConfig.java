package com.app.novastore.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationGlobalConfig {

    @Value("${springdoc.api-docs.path}")
    private String springDocApiDocPath;

    @Value("${springdoc.swagger-ui.path}")
    private String springDocSwaggerPath;


    /**
     * HMAC key used to sign and verify every JWT this service issues.
     */
    @Bean
    public SecretKey jwtSigningKey(NovastoreProperties properties) {
        byte[] keyBytes = Decoders.BASE64.decode(
                properties.getSecurity().getAuthentication().getJwt().getBase64Secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Bean
    public JwtParser jwtParser(SecretKey jwtSigningKey) {
        return Jwts.parser().verifyWith(jwtSigningKey).build();
    }

    /**
     * CORS rules for the security filter chain to apply.
     * <p>
     * Deliberately a {@link CorsConfigurationSource} and not a standalone {@code CorsFilter}
     * bean: Boot registers a bare filter bean at the lowest precedence, which puts it behind
     * the security chain (order -100). A preflight OPTIONS would then be rejected by
     * authorization before any CORS header was written. {@code http.cors(...)} runs this
     * source inside the chain, ahead of the authorization checks.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(NovastoreProperties properties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = properties.getCors();
        if (!CollectionUtils.isEmpty(config.getAllowedOrigins())) {
            log.debug("Registering CORS configuration for {}", config.getAllowedOrigins());
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration(springDocApiDocPath, config);
            source.registerCorsConfiguration(springDocSwaggerPath + "/**", config);
        }
        return source;
    }

    @Bean
    public MinioClient minioClient(NovastoreProperties properties) {
        log.info("minio client connecting to {}", properties.getObjectStorage().getMinio().getPath().getEndPointUrl());
        return MinioClient.builder()
                .endpoint(properties.getObjectStorage().getMinio().getPath().getEndPointUrl())
                .credentials(properties.getObjectStorage().getMinio().getAuth().getAccessKey(), properties.getObjectStorage().getMinio().getAuth().getSecretKey())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "novastore.pricing-hub.enabled", havingValue = "true")
    public RestClient priceHubRestClient(RestClient.Builder builder, NovastoreProperties properties) {
        NovastoreProperties.PricingHub hub = properties.getPricingHub();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(hub.getConnectTimeoutMillis()));
        factory.setReadTimeout(Duration.ofMillis(hub.getReadTimeoutMillis()));

        log.info("pricing hub client pointing at {}", hub.getUrl());

        RestClient.Builder configured = builder
                .baseUrl(hub.getUrl())
                .requestFactory(factory);

        if (StringUtils.hasText(hub.getApiKey())) {
            configured.defaultHeader("X-API-Key", hub.getApiKey());
        } else {
            log.warn("pricing hub api key is blank - calls will fail once the hub enforces X-API-Key");
        }

        return configured.build();
    }
}
