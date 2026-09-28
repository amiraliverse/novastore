package com.app.novastore.security.session.vm;

import com.app.novastore.security.session.Session;
import com.app.novastore.util.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface SessionMapper extends EntityMapper<SessionVM, Session> {
    @Override
    @Mapping(target = "isExpired", expression = "java(isTokenExpired(entity))")
    SessionVM toViewModel(Session entity);

    default Boolean isTokenExpired(Session entity) {
        return entity.getDate().getTime() + entity.getExpiredIn() - new Date().getTime() <= 0;
    }
}
