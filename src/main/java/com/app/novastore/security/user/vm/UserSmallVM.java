package com.app.novastore.security.user.vm;

import com.app.novastore.security.user.gender.UserGender;
import com.app.novastore.security.user.level.UserLevel;
import com.app.novastore.security.user.type.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UserSmallVM {
    @NotBlank
    private String id;
    @NotBlank
    private String firstName;
    private String lastName;
    private String fullName;
    private String nickName;
    private String username;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mobile;
    private String imageUrl;
    private Double imageRatio;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserLevel level;
    @NotNull
    private UserType type;
    private UserGender gender;
    private Boolean active;
    private Boolean verified;
    private Boolean deleted;
    /** When the account was created. Read-only, taken from the audit {@code createdDate}. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant joinedAt;
}
