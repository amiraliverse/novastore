package com.app.novastore.security.session;

import com.app.novastore.security.token.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Date;

@RedisHash(value = "GOLDSTAR_SESSIONS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Session {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String ip;
    private BrowserType browserType;
    private String browserName;
    private OperatingSystemType operatingSystemType;
    private String operatingSystemName;
    private String idToken;
    @TimeToLive
    private Long expiredIn;
    private TokenType tokenType;
    private String refreshToken;
    private Date date;

    @Transient
    private Boolean isCurrentDevice;
}
