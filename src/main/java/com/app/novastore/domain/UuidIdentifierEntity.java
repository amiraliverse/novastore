package com.app.novastore.domain;

import com.app.novastore.annotations.uuid.Uuid;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@MappedSuperclass
public class UuidIdentifierEntity extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Uuid
    @Column(name = "ID", updatable = false, nullable = false)
    private String id;
}
