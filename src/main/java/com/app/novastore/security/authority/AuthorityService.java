package com.app.novastore.security.authority;

import com.app.novastore.hibernate.SearchOption;
import com.app.novastore.hibernate.impl.AbstractJpaServiceDefaultImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthorityService extends AbstractJpaServiceDefaultImpl<Authority, Long, AuthorityRepository> {

    private final AuthoritySpecification specification;

    public AuthorityService(AuthorityRepository repository, AuthoritySpecification specification) {
        super(repository);
        this.specification = specification;
    }

    public Page<Authority> findAll(SearchOption<Authority> searchOption) {
        return repository.findAll(specification.getSpecification(searchOption.getFilter()), searchOption.getPageable());
    }
}
