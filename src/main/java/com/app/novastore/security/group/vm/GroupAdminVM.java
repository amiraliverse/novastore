package com.app.novastore.security.group.vm;

import com.app.novastore.domain.vm.IdentityIdentifierVM;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupAdminVM extends IdentityIdentifierVM {

    @NotBlank
    private String name;

    private String internalName;

    private Boolean active;
}
