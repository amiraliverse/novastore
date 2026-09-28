package com.app.novastore.security.token.invalidation;

import com.app.novastore.constants.NovastoreConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * A JWT that must no longer be accepted even though its signature and expiry still check out
 * (logout, session revocation).
 * <p>
 * The id is a SHA-256 digest of the token, not the token itself: this hash is readable by
 * anyone with Redis access, and a stored credential would be replayable. The digest is enough
 * to recognise a token that comes back.
 * <p>
 * The TTL is the token's own remaining lifetime. Past its {@code exp} the signature check
 * rejects it anyway, so keeping the entry longer would only grow the keyspace.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = NovastoreConstants.REDIS_HASH_PREFIX + "INVALIDATED_TOKEN")
public class InvalidatedToken {

    @Id
    private String id;

    private String userId;

    @TimeToLive
    private Long timeToLive;
}
