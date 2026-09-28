package com.app.novastore.security.session;

import com.app.novastore.annotations.auth.AutoPreAuthorize;
import com.app.novastore.security.session.vm.SessionMapper;
import com.app.novastore.security.session.vm.SessionVM;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/sessions")
public class SessionResource {

    private final SessionService sessionService;
    private final SessionMapper mapper;

    @AutoPreAuthorize("hasAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    @GetMapping("/v1/find")
    public ResponseEntity<List<SessionVM>> findAllForCurrentUser() {
        return new ResponseEntity<>(mapper.toViewModel(sessionService.findAllForCurrentUser()), HttpStatus.OK);
    }

    @AutoPreAuthorize("hasAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    @DeleteMapping("/v1/delete/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable String id) {
        return new ResponseEntity<>(sessionService.deleteByIdAndMakeTokenInvalid(id), HttpStatus.OK);
    }

    @AutoPreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/v1/admin/find")
    public ResponseEntity<List<SessionVM>> findAllByUserId(@RequestParam String userId) {
        return new ResponseEntity<>(mapper.toViewModel(sessionService.findAllByUserId(userId)), HttpStatus.OK);
    }
}
