package com.app.novastore.hibernate.impl;

import com.app.novastore.hibernate.AbstractJpaService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public abstract class AbstractJpaServiceDefaultImpl<T, ID, R extends JpaRepository<T, ID>> implements AbstractJpaService<T, ID> {

    protected final R repository;

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public T persist(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<T> findAll(Example<T> example) {
        return repository.findAll(example);
    }

    @Override
    public Page<T> findAll(Example<T> example, Pageable pageable) {
        return repository.findAll(example, pageable);
    }

    @Override
    public List<T> findAll() { return repository.findAll(); }
}
