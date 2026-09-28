package com.app.novastore.security.authority.vm;

import com.app.novastore.security.authority.Authority;
import com.app.novastore.util.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorityAdminMapper extends EntityMapper<AuthorityAdminVM, Authority> {
}
