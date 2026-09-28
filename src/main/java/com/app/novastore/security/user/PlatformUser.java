package com.app.novastore.security.user;

import com.app.novastore.domain.UuidIdentifierEntity;
import com.app.novastore.security.group.Group;
import com.app.novastore.security.user.gender.UserGender;
import com.app.novastore.security.user.gender.converter.UserGenderConverter;
import com.app.novastore.security.user.level.UserLevel;
import com.app.novastore.security.user.level.converter.UserLevelConverter;
import com.app.novastore.security.user.type.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class PlatformUser extends UuidIdentifierEntity implements UserDetails {

    @Enumerated(EnumType.STRING)
    private UserType type;

    private String firstName;

    private String lastName;

    private String nickName;

    @Column(unique = true)
    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private String mobile;

    private String encryptedNationalNumber;

    private Date birthDate;

    private String persianBirthDate;

    @Convert(converter = UserLevelConverter.class)
    @Column(name = "user_level")
    private UserLevel level;

    @Convert(converter = UserGenderConverter.class)
    private UserGender gender;

    private String imageUrl;

    private Double imageRatio;

    private String description;

    private Boolean active;

    private Boolean verified;

    private Boolean deleted;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"})
    )
    @Builder.Default
    private Set<Group> groups = new HashSet<>();

    @Transient
    private String stringAuthorities;

    public PlatformUser(String id,
                        String username,
                        String stringAuthorities,
                        UserType type) {
        super.setId(id);
        this.username = username;
        this.stringAuthorities = stringAuthorities;
        this.type = type;
    }

    @JsonIgnore
    public String getAuthoritiesAsString() {
        String authorities = "";

        if (groups != null && !groups.isEmpty()) {
            authorities = getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")).concat(",");
        }

        return authorities
                .concat("ROLE_")
                .concat(type.name());
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return groups.stream()
                .map(Group::getAuthorities)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return active;
    }

    public String getFullName() {
        if ((firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty())) {
            if ((firstName != null && !firstName.isEmpty()) && (lastName != null && !lastName.isEmpty()))
                return firstName + " " + lastName;
            else if ((lastName != null && !lastName.isEmpty()))
                return lastName;
            else
                return firstName;
        }
        return null;
    }
}
