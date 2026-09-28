package com.app.novastore.security.user;

import com.app.novastore.annotations.auth.AutoPreAuthorize;
import com.app.novastore.hibernate.SearchOption;
import com.app.novastore.security.SecurityUtils;
import com.app.novastore.security.authority.vm.AuthorityAdminMapper;
import com.app.novastore.security.authority.vm.AuthorityAdminVM;
import com.app.novastore.security.user.vm.UserSmallMapper;
import com.app.novastore.security.user.vm.UserSmallVM;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserResource {

    private final UserService userService;
    private final UserSmallMapper smallMapper;

    private final AuthorityAdminMapper authorityMapper;

    @AutoPreAuthorize("hasAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    @PostMapping("/v1/persist")
    public ResponseEntity<UserSmallVM> protectedPersist(@RequestBody @Valid UserSmallVM vm) {
        return new ResponseEntity<>(smallMapper.toViewModel(
                userService.protectedPersist(
                        smallMapper.toEntity(vm))),
                HttpStatus.OK);
    }

    @AutoPreAuthorize("hasAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    @GetMapping("/v1/find")
    public ResponseEntity<UserSmallVM> findCurrentUser() {
        return new ResponseEntity<>(smallMapper.toViewModel(
                userService.findActiveByIdAndDeletedIsFalse(SecurityUtils.getCurrentUserId())),
                HttpStatus.OK);
    }

    @AutoPreAuthorize
    @PostMapping("/v1/find-all")
    public ResponseEntity<Page<UserSmallVM>> findAll(@RequestBody SearchOption<UserSmallVM> searchOption) {
        return new ResponseEntity<>(smallMapper.toViewModel(
                userService.findAll(new SearchOption<PlatformUser>().from(searchOption, smallMapper))),
                HttpStatus.OK);
    }

    @AutoPreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/v1/authorities")
    public ResponseEntity<List<AuthorityAdminVM>> findCurrentUserAuthority() {
        return new ResponseEntity<>(
                authorityMapper.toViewModel(userService.findAuthoritiesById(SecurityUtils.getCurrentUserId())),
                HttpStatus.OK);
    }
}
