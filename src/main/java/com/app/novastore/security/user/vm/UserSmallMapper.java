package com.app.novastore.security.user.vm;

import com.app.novastore.security.user.PlatformUser;
import com.app.novastore.util.EntityMapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserSmallMapper extends EntityMapper<UserSmallVM, PlatformUser> {

    @Override
    @Mapping(target = "joinedAt", source = "createdDate")
    UserSmallVM toViewModel(PlatformUser entity);

    /** {@code joinedAt} is audit data - never written back from a request body. */
    @Override
    @Mapping(target = "createdDate", ignore = true)
    PlatformUser toEntity(UserSmallVM dto);
}
