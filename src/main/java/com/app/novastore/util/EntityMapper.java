package com.app.novastore.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface EntityMapper<V, E> {

    E toEntity(V dto);

    V toViewModel(E entity);

    List<E> toEntity(List<V> dtoList);

    List<V> toViewModel(List<E> entityList);

    default Page<V> toViewModel(Page<E> entity) {
        PageRequest pageRequest = PageRequest.of(entity.getNumber(), entity.getSize());
        return new PageImpl<>(toViewModel(entity.getContent()), pageRequest, entity.getTotalElements());
    }

    default Page<E> toEntity(Page<V> viewModel) {
        PageRequest pageRequest = PageRequest.of(viewModel.getNumber(), viewModel.getSize());
        return new PageImpl<>(toEntity(viewModel.getContent()), pageRequest, viewModel.getTotalElements());
    }

}
