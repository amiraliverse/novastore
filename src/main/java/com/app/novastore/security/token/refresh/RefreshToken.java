package com.app.novastore.security.token.refresh;

import com.app.novastore.constants.NovastoreConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = NovastoreConstants.REDIS_HASH_PREFIX + "REFRESH_TOKEN")
public class RefreshToken {

    @Id
    private String id;

    private String userId;

    @TimeToLive
    private Long timeToLive;

    @Override
    public String toString() {
        return this.id;
    }

    public static RefreshToken forUserId(String userId, Long timeToLive) {
        return new RefreshToken(UUID.randomUUID().toString(), userId, timeToLive);
    }

    private RefreshToken(String id, String userId, Long timeToLive) {
        this.id = id;
        this.userId = userId;
        this.timeToLive = timeToLive;
    }

}
