package com.app.novastore.security.authority.vm;

import com.app.novastore.domain.vm.IdentityIdentifierVM;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorityAdminVM extends IdentityIdentifierVM {
    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String icon;

    private Boolean active;

    @JsonProperty("authorityCode")
    public String getAuthorityCode() {
        return code;
    }
}
