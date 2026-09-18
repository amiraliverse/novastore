package com.app.novastore.domain.vm;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@MappedSuperclass
public class UuidIdentifierVM extends AbstractAuditingVM {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
}
