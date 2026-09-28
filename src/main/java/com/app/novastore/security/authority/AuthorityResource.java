package com.app.novastore.security.authority;

import com.app.novastore.annotations.auth.AutoPreAuthorize;
import com.app.novastore.hibernate.SearchOption;
import com.app.novastore.security.authority.vm.AuthorityAdminMapper;
import com.app.novastore.security.authority.vm.AuthorityAdminVM;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/authority")
public class AuthorityResource {

    private final AuthorityService authorityService;
    private final AuthorityAdminMapper mapper;

    @AutoPreAuthorize
    @PostMapping("/v1/persist")
    public ResponseEntity<AuthorityAdminVM> persist(@RequestBody @Valid AuthorityAdminVM authorityVM) {
        return new ResponseEntity<>(mapper.toViewModel(
                authorityService.persist(mapper.toEntity(authorityVM))),
                HttpStatus.OK);
    }

    @AutoPreAuthorize
    @PostMapping("/v1/find")
    public ResponseEntity<Page<AuthorityAdminVM>> findAll(@RequestBody SearchOption<AuthorityAdminVM> searchOption) {
        return new ResponseEntity<>(mapper.toViewModel(
                authorityService.findAll(new SearchOption<Authority>().from(searchOption, mapper))),
                HttpStatus.OK);
    }

    @AutoPreAuthorize
    @DeleteMapping("/v1/delete/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable @Param("id") long id) {
        authorityService.deleteById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
