package com.app.novastore.security.user;

import com.app.novastore.hibernate.AbstractJpaSpecification;
import com.app.novastore.security.user.gender.UserGender;
import com.app.novastore.security.user.level.UserLevel;
import com.app.novastore.security.user.type.UserType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification implements AbstractJpaSpecification<PlatformUser> {
    @Override
    public Specification<PlatformUser> getSpecification(PlatformUser platformUser) {
        Specification<PlatformUser> spec = Specification.unrestricted();

        if (isPresent(platformUser.getId())) {
            spec = spec.and(hasId(platformUser.getId()));
        }

        if (isPresent(platformUser.getFirstName())) {
            spec = spec.and(hasFirstName(platformUser.getFirstName()));
        }

        if (isPresent(platformUser.getLastName())) {
            spec = spec.and(hasLastName(platformUser.getLastName()));
        }

        if (isPresent(platformUser.getNickName())) {
            spec = spec.and(hasNickName(platformUser.getNickName()));
        }

        if (isPresent(platformUser.getUsername())) {
            spec = spec.and(hasUsername(platformUser.getUsername()));
        }

        if (isPresent(platformUser.getEmail())) {
            spec = spec.and(hasEmail(platformUser.getEmail()));
        }

        if (isPresent(platformUser.getMobile())) {
            spec = spec.and(hasMobile(platformUser.getMobile()));
        }

        if (isPresent(platformUser.getImageUrl())) {
            spec = spec.and(hasImageUrl(platformUser.getImageUrl()));
        }

        if (isPresent(platformUser.getImageRatio())) {
            spec = spec.and(hasImageRatio(platformUser.getImageRatio()));
        }

        if (isPresent(platformUser.getDescription())) {
            spec = spec.and(hasDescription(platformUser.getDescription()));
        }

        if (isPresent(platformUser.getLevel())) {
            spec = spec.and(hasLevel(platformUser.getLevel()));
        }

        if (isPresent(platformUser.getType())) {
            spec = spec.and(hasType(platformUser.getType()));
        }

        if (isPresent(platformUser.getGender())) {
            spec = spec.and(hasGender(platformUser.getGender()));
        }

        if (isPresent(platformUser.getActive())) {
            spec = spec.and(hasActive(platformUser.getActive()));
        }

        if (isPresent(platformUser.getVerified())) {
            spec = spec.and(hasVerified(platformUser.getVerified()));
        }

        if (isPresent(platformUser.getDeleted())) {
            spec = spec.and(hasDeleted(platformUser.getDeleted()));
        }

        return spec;
    }

    private Specification<PlatformUser> hasId(String id) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.id), id);
    }

    private Specification<PlatformUser> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(PlatformUser_.firstName)), "%" + firstName.toLowerCase() + "%");
    }

    private Specification<PlatformUser> hasLastName(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(PlatformUser_.lastName)), "%" + lastName.toLowerCase() + "%");
    }

    private Specification<PlatformUser> hasNickName(String nickName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(PlatformUser_.nickName)), "%" + nickName.toLowerCase() + "%");
    }

    private Specification<PlatformUser> hasUsername(String username) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(PlatformUser_.username)), "%" + username.toLowerCase() + "%");
    }

    private Specification<PlatformUser> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get(PlatformUser_.email)), email.toLowerCase());
    }

    private Specification<PlatformUser> hasMobile(String mobile) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.mobile), mobile);
    }

    private Specification<PlatformUser> hasImageUrl(String imageUrl) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(PlatformUser_.imageUrl)), "%" + imageUrl.toLowerCase() + "%");
    }

    private Specification<PlatformUser> hasImageRatio(Double imageRatio) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.imageRatio), imageRatio);
    }

    private Specification<PlatformUser> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(PlatformUser_.description)), "%" + description.toLowerCase() + "%");
    }

    private Specification<PlatformUser> hasLevel(UserLevel level) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.level), level);
    }

    private Specification<PlatformUser> hasType(UserType type) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.type), type);
    }

    private Specification<PlatformUser> hasGender(UserGender gender) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.gender), gender);
    }

    private Specification<PlatformUser> hasActive(Boolean active) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.active), active);
    }

    private Specification<PlatformUser> hasVerified(Boolean verified) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.verified), verified);
    }

    private Specification<PlatformUser> hasDeleted(Boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get(PlatformUser_.deleted), deleted);
    }
}
