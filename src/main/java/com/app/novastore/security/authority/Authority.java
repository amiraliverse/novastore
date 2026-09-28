package com.app.novastore.security.authority;

import com.app.novastore.domain.IdentityIdentifierEntity;
import com.app.novastore.security.group.Group;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "authority")
public class Authority extends IdentityIdentifierEntity implements GrantedAuthority {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    private String icon;

    private Boolean active;

    @ManyToMany(mappedBy = "authorities")
    private Set<Group> groups = new HashSet<>();

    @Override
    public String getAuthority() {
        return code;
    }
}
