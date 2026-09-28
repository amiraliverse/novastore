package com.app.novastore.annotations.uuid;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;

public class UuidGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
