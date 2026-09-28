package com.app.novastore.security.user;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.exception.RecordNotFoundException;
import com.app.novastore.exception.UserIsDisabledException;
import com.app.novastore.hibernate.SearchOption;
import com.app.novastore.hibernate.impl.AbstractJpaServiceDefaultImpl;
import com.app.novastore.security.SecurityUtils;
import com.app.novastore.security.authority.Authority;
import com.app.novastore.security.user.gender.UserGender;
import com.app.novastore.security.user.type.UserType;
import com.app.novastore.util.OptionalString;
import com.app.novastore.util.TSIDUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.app.novastore.security.user.UserCacheConstants.*;

//import static com.app.novastore.security.user.UserCacheConstants.*;

@Service
@Transactional(readOnly = true)
public class UserService extends AbstractJpaServiceDefaultImpl<PlatformUser, String, UserRepository> {

    private final NovastoreProperties properties;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, NovastoreProperties properties, PasswordEncoder passwordEncoder) {
        super(repository);
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PlatformUser protectedPersist(final PlatformUser platformUser) {
        if (SecurityUtils.getCurrentUser().getType().equals(UserType.CUSTOMER)) {
            platformUser.setId(SecurityUtils.getCurrentUserId());
        }

        return OptionalString.ofNullable(platformUser.getId())
                .map(userId ->
                        findByIdAndDeletedIsFalse(userId)
                                .map(dbUser -> {
                                    if (!dbUser.getActive())
                                        throw new UserIsDisabledException();

                                    if (StringUtils.hasText(platformUser.getPassword()))
                                        dbUser.setPassword(passwordEncoder.encode(platformUser.getPassword()));

                                    if (StringUtils.hasText(platformUser.getUsername()))
                                        dbUser.setUsername(platformUser.getUsername());

                                    dbUser.setFirstName(platformUser.getFirstName());
                                    dbUser.setLastName(platformUser.getLastName());
                                    dbUser.setNickName(platformUser.getNickName());
                                    dbUser.setImageUrl(platformUser.getImageUrl());

                                    return persist(dbUser);
                                }).orElseGet(() -> {
                                    return persistWithDefaultConfig(platformUser);
                                }))
                .orElseGet(() -> {
                    return persistWithDefaultConfig(platformUser);
                });
    }

    @Transactional
    public PlatformUser persistWithDefaultConfig(final PlatformUser platformUser) {
        platformUser.setUsername(resolvePreferredUsername(platformUser));
        platformUser.setPassword(resolvePreferredPassword(platformUser));
        platformUser.setActive(properties.getRules().getUserRegistration().getUserActivationDefaultState());
        platformUser.setLevel(properties.getRules().getUserRegistration().getUserLevelDefaultState());
        platformUser.setGender(platformUser.getGender() == null ? UserGender.UNKNOWN : platformUser.getGender());
        platformUser.setVerified(platformUser.getVerified() == null || platformUser.getVerified());
        platformUser.setDeleted(platformUser.getDeleted() == null || platformUser.getDeleted());
        return persist(platformUser);
    }

    public Optional<PlatformUser> findByUsername(String id) {
        return repository.findByUsername(id);
    }

    @Cacheable(cacheNames = USER_CACHE_NAME + USER_FIND_BY_ID_CACHE_NAME, key = "#p0")
    public PlatformUser findActiveByIdAndDeletedIsFalse(String id) {
        return repository.findByIdAndDeletedIsFalse(id)
                .map(user -> {
                    if (!user.getActive())
                        throw new UserIsDisabledException();

                    return user;
                })
                .orElseThrow(RecordNotFoundException::new);
    }

    public Optional<PlatformUser> findByIdAndDeletedIsFalse(String id) {
        return repository.findByIdAndDeletedIsFalse(id);
    }

    @Cacheable(cacheNames = AUTHORITIES_CACHE_NAME + AUTHORITIES_FIND_BY_USER_ID_CACHE_NAME, key = "#p0")
    public List<Authority> findAuthoritiesById(String id) {
        return (List<Authority>) findActiveByIdAndDeletedIsFalse(id).getAuthorities();
    }

    public Optional<PlatformUser> findByUsernameAndTypeAndDeletedIsFalse(String username, UserType type) {
        return repository.findByUsernameAndTypeAndDeletedIsFalse(username, type);
    }

    public Optional<PlatformUser> findByUsernameAndDeletedIsFalse(String username) {
        return repository.findByUsernameAndDeletedIsFalse(username);
    }

    public Optional<PlatformUser> findByMobileAndTypeAndDeletedIsFalse(String mobile, UserType type) {
        return repository.findByMobileAndTypeAndDeletedIsFalse(mobile, type);
    }

    @Override
    public void deleteById(String id) {
        int affected = repository.softDeleteById(id);
//        if (affected == 1)
//            return id;
//        else
//            return null;
    }

    private String resolvePreferredUsername(final PlatformUser platformUser) {
        return OptionalString.ofNullable(platformUser.getUsername())
                .orElse(OptionalString.ofNullable(platformUser.getMobile())
                        .orElse(OptionalString.ofNullable(platformUser.getEmail())
                                .orElse(TSIDUtils.randomString())));
    }

    private String resolvePreferredPassword(final PlatformUser platformUser) {
        String plainPassword = OptionalString.ofNullable(platformUser.getPassword())
                .orElse(OptionalString.ofNullable(platformUser.getMobile())
                        .map(mobile -> org.apache.commons.lang3.StringUtils.right(mobile, 4))
                        .orElse(OptionalString.ofNullable(platformUser.getEmail())
                                .map(email -> org.apache.commons.lang3.StringUtils.left(platformUser.getEmail(), 8))
                                .orElse(org.apache.commons.lang3.StringUtils.EMPTY)));
        return passwordEncoder.encode(plainPassword);
    }

    public Page<PlatformUser> findAll(SearchOption<PlatformUser> searchOption) {
        return repository.findAll(Example.of(searchOption.getFilter()), searchOption.getPageable());
    }
}
