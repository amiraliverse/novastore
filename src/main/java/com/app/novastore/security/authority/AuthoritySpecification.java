package com.app.novastore.security.authority;

import com.app.novastore.hibernate.AbstractJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthoritySpecification implements AbstractJpaSpecification<Authority> {
    @Override
    public Specification<Authority> getSpecification(Authority authority) {
        Specification<Authority> spec = Specification.unrestricted();

        if (isPresent(authority.getName())) {
            spec = spec.and(hasName(authority.getName()));
        }

        if (isPresent(authority.getCode())) {
            spec = spec.and(hasCode(authority.getCode()));
        }

        return spec;
    }

    private Specification<Authority> hasName(String name) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(Authority_.name), "%" + name + "%"));
    }

    private Specification<Authority> hasCode(String code) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Authority_.code), code));
    }
}
