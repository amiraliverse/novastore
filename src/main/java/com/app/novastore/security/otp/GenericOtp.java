package com.app.novastore.security.otp;

import com.app.novastore.constants.NovastoreConstants;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = NovastoreConstants.REDIS_HASH_PREFIX + "OTP")
public class GenericOtp {

    @Id
    private String id;

    private String identifier;

    private String code;

    private String accessKey;

    @TimeToLive
    private Long timeToLive;
}
