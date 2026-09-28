package com.app.novastore.hibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface AbstractJpaSpecification<T> {

    Specification<T> getSpecification(T entity);

    default <R extends Comparable<R>> Predicate getRangePredicate(Root<T> root, CriteriaBuilder cb, SingularAttribute<T, R> attribute, R from, R to) {
        return getPredicate(root.get(attribute), cb, from, to);
    }

    default <R extends Comparable<R>> Predicate getRangePredicate(Root<T> root, CriteriaBuilder cb, String attribute, R from, R to) {
        return getPredicate(root.get(attribute), cb, from, to);
    }

    default <R extends Comparable<R>> Predicate getPredicate(Path<R> path, CriteriaBuilder cb, R from, R to) {
        if (from != null && to != null) {
            return cb.between(path, from, to);
        } else if (from != null) {
            return cb.greaterThanOrEqualTo(path, from);
        } else if (to != null) {
            return cb.lessThanOrEqualTo(path, to);
        }
        return cb.conjunction();
    }

    default boolean isPresent(String s) {
        return s != null && !s.trim().isEmpty();
    }

    default boolean isPresent(Object o) {
        return o != null && !o.toString().trim().isEmpty();
    }

    default boolean isPresent(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
