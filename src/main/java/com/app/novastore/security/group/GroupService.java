package com.app.novastore.security.group;

import com.app.novastore.hibernate.SearchOption;
import com.app.novastore.hibernate.impl.AbstractJpaServiceDefaultImpl;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GroupService extends AbstractJpaServiceDefaultImpl<Group, Long, GroupRepository> {

    public GroupService(GroupRepository repository) {
        super(repository);
    }

    public Page<Group> findAll(SearchOption<Group> searchOption) {
        return repository.findAll(Example.of(searchOption.getFilter()), searchOption.getPageable());
    }

    @Transactional
    public Group protectedPersist(final Group group) {
        group.setActive(group.getActive() == null || group.getActive());
        return persist(group);
    }
}
