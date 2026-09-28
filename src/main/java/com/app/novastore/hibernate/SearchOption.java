package com.app.novastore.hibernate;

import com.app.novastore.util.EntityMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchOption<T> {
    private static final int DEFAULT_PAGE_SIZE = 20;

    @NotNull
    @Valid
    private T filter;

    private String order;

    private int pageNumber;

    @Min(value = 1)
    @Max(value = 50)
    private int pageSize;

    private String sortField;

    private String sortClass;

    private Sort.Direction direction;

    public <V> SearchOption<T> from(SearchOption<? extends V> newOption, EntityMapper<V, T> mapper) {
        this.order = newOption.order;
        this.pageNumber = newOption.pageNumber;
        this.pageSize = newOption.pageSize;
        this.sortField = newOption.sortField;
        this.sortClass = newOption.sortClass;
        this.direction = newOption.direction;
        this.filter = mapper.toEntity(newOption.filter);
        return this;
    }

    public Pageable getPageable() {
        PageRequest pageable;
        if (this.getSortField() != null && !this.getSortField().isEmpty()) {
            pageable = PageRequest.of(
                    this.getPageNumber(),
                    this.resolvedPageSize(),
                    this.getDirection(),
                    this.getSortField()
            );
        } else {
            pageable = PageRequest.of(
                    this.getPageNumber(),
                    this.resolvedPageSize());
        }
        return pageable;
    }

    private int resolvedPageSize() {
        return this.pageSize < 1 ? DEFAULT_PAGE_SIZE : this.pageSize;
    }
}
