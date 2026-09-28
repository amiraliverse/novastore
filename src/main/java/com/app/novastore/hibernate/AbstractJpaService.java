package com.app.novastore.hibernate;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AbstractJpaService<T, ID> {
    Optional<T> findById(ID id);

    boolean existsById(ID id);

    T persist(T entity);

    void deleteById(ID id);

    Page<T> findAll(Pageable pageable);

    List<T> findAll(Example<T> example);

    Page<T> findAll(Example<T> example, Pageable pageable);

    List<T> findAll();
}
