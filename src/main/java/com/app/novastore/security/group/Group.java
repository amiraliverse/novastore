package com.app.novastore.security.group;

import com.app.novastore.domain.IdentityIdentifierEntity;
import com.app.novastore.security.authority.Authority;
import com.app.novastore.security.user.PlatformUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "groups")
public class Group extends IdentityIdentifierEntity {

    @Column(nullable = false)
    private String name;

    private String internalName;

    private Boolean active;

    // should be changed to individual table with OneToMany relation
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "groups_authority",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "authority_id"})
    )
    private Set<Authority> authorities = new HashSet<>();

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private Set<PlatformUser> users = new HashSet<>();
}