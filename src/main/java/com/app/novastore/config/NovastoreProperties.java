package com.app.novastore.config;

import com.app.novastore.security.user.level.UserLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "novastore", ignoreUnknownFields = false)
@Getter
@Setter
public class NovastoreProperties {

    private CorsConfiguration cors = new CorsConfiguration();
    private Rules rules = new Rules();
    @Valid
    private Security security = new Security();
    private ObjectStorage objectStorage = new ObjectStorage();
    private App app = new App();
    private MessageTemplate messageTemplate = new MessageTemplate();
    private Jobs jobs = new Jobs();
    private PricingHub pricingHub = new PricingHub();

    /**
     * novastore-pricing-hub, the service that owns every price provider integration.
     *
     * <p>{@code apiKey} goes out as {@code X-API-Key}. The hub ships with enforcement off
     * ({@code pricing-hub.api.require-key=false}) so a fresh checkout works, which makes it easy
     * to deploy this side with a blank key and not notice until the hub is locked down.
     */
    @Getter
    @Setter
    public static class PricingHub {
        /** False keeps StubPriceFeedClient in place, so nothing needs a hub to run locally. */
        private Boolean enabled = false;
        private String url;
        private String apiKey;
        /**
         * Kept short on purpose: a hung hub must not hold this service's request threads.
         *
         * <p>Primitive with an in-type default so a partial override (a profile or a
         * {@code CONFIG_} env var restating only one of the two) still leaves the other one set,
         * rather than {@code Duration.ofMillis(null)} failing startup with an NPE.
         */
        private int connectTimeoutMillis = 2000;
        private int readTimeoutMillis = 3000;
        /**
         * Drop a {@code STALE} hub quote instead of ingesting it. Nothing downstream re-checks a
         * tick's age, so leaving this on is what keeps a frozen bazaar reference from being
         * margined and served as a live price. Off only pins the current (permissive) behaviour.
         */
        private boolean rejectStale = true;
        /**
         * {@code asset.code} to hub market code, e.g. {@code GOLD -> GOLD_18K_GRAM:IRR}.
         *
         * <p>Configuration rather than a column on {@code asset} because it is a handful of rows
         * that change when a market is added, not per asset business data — and because the hub's
         * own lesson is that this mapping is data, not code. An asset with no entry here is simply
         * not priced.
         */
        private Map<String, String> markets = new HashMap<>();
    }

    /**
     * Scheduling for every background job. Each job carries its own cron and an enable
     * flag; a job with no cron of its own falls back to {@link #defaultCron}, whose "-"
     * value is Spring's disabled-cron marker, so an unconfigured job simply never runs.
     */
    @Getter
    @Setter
    public static class Jobs {

        /** Spring's disabled-cron marker: a job that resolves to this never fires. */
        private String defaultCron = "-";
        private Job orderExpiry = new Job();
        private Job dailyRollup = new Job();
        private Job priceFeed = new Job();
        private Job tickRetention = new Job();

        @Getter
        @Setter
        public static class Job {
            private String cron;
            private boolean enabled;
        }
    }

    @Getter
    @Setter
    public static class Rules {
        private UserRegistration userRegistration = new UserRegistration();

        @Getter
        @Setter
        public static class UserRegistration {
            private Boolean userActivationDefaultState;
            private UserLevel userLevelDefaultState;
        }
    }


    @Getter
    @Setter
    public static class Security {

        @Valid
        private Authentication authentication = new Authentication();
        private Cookie cookie = new Cookie();
        private Otp otp = new Otp();

        @Getter
        @Setter
        public static class Authentication {

            @Valid
            private Jwt jwt = new Jwt();
            private RefreshToken refreshToken = new RefreshToken();

            @Setter
            @Getter
            public static class Jwt {
                private String authoritiesKey;
                private String userIdKey;
                private String usernameKey;
                private String mobileKey;
                private String tenantKey;
                private String driverKey;
                private String userTypeKey;
                private String sessionIdKey;
                private String consumerKey;
                private String consumerValue;
                @NotBlank
                private String base64Secret;
                private Long tokenValidityInSeconds;
            }

            @Getter
            @Setter
            public static class RefreshToken {
                private Boolean enabled;
                private Long tokenValidityInSeconds;
            }
        }

        @Getter
        @Setter
        public static class Cookie {
            private String jwtTokenKey;
            private String refreshTokenKey;
            private Boolean httpOnly;
            private Boolean secure;
            private Integer maxAge;
        }

        @Getter
        @Setter
        public static class Otp {
            private int length;
            private long expirationTimeInSeconds;
            private String[] patterns;
        }
    }

    @Getter
    @Setter
    public static class ObjectStorage {
        private Minio minio = new Minio();

        @Getter
        @Setter
        public static class Minio {
            private Path path = new Path();
            private Auth auth = new Auth();

            private String bucketName;

            @Getter
            @Setter
            public static class Path {
                private String endPointUrl;
                private String images;
                private String users;
                private String drivers;
            }

            @Getter
            @Setter
            public static class Auth {
                private String accessKey;
                private String secretKey;
            }
        }
    }

    @Getter
    @Setter
    public static class App {
        private String appName;
    }

    @Getter
    @Setter
    public static class MessageTemplate {
        private String otpMessageTemplate;
    }
}
