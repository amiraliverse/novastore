package com.app.novastore.security.user;

import com.app.novastore.security.user.type.UserType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static com.app.novastore.security.user.UserCacheConstants.USER_CACHE_NAME;
import static com.app.novastore.security.user.UserCacheConstants.USER_FIND_BY_ID_CACHE_NAME;

public interface UserRepository extends JpaRepository<PlatformUser, String>, JpaSpecificationExecutor<PlatformUser> {
    @Override
    @CacheEvict(cacheNames = USER_CACHE_NAME + USER_FIND_BY_ID_CACHE_NAME, key = "#entity.id", condition = "#entity.id != null && #entity.id.length() > 0")
    <S extends PlatformUser> S save(S entity);

    Optional<PlatformUser> findByUsername(String username);

    Optional<PlatformUser> findByMobileAndTypeAndDeletedIsFalse(String mobile, UserType userType);

    Optional<PlatformUser> findByUsernameAndTypeAndDeletedIsFalse(String username, UserType userType);

    Optional<PlatformUser> findByUsernameAndDeletedIsFalse(String username);

    Optional<PlatformUser> findByIdAndDeletedIsFalse(String id);

    @Modifying
    @Query("update PlatformUser u set u.deleted = true where u.id = :id")
    int softDeleteById(String id);
}
