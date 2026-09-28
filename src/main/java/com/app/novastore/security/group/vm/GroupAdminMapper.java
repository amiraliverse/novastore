package com.app.novastore.security.group.vm;

import com.app.novastore.security.group.Group;
import com.app.novastore.util.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupAdminMapper extends EntityMapper<GroupAdminVM, Group> {
    Group toEntity(GroupAdminVM dto);
}
