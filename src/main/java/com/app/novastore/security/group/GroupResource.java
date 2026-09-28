package com.app.novastore.security.group;

import com.app.novastore.annotations.auth.AutoPreAuthorize;
import com.app.novastore.hibernate.SearchOption;
import com.app.novastore.security.group.vm.GroupAdminMapper;
import com.app.novastore.security.group.vm.GroupAdminVM;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/group")
public class GroupResource {

    private final GroupService groupService;
    private final GroupAdminMapper mapper;

    @AutoPreAuthorize
    @PostMapping("/v1/persist")
    public ResponseEntity<GroupAdminVM> persist(@RequestBody @Valid GroupAdminVM groupVM) {
        return new ResponseEntity<>(mapper.toViewModel(
                groupService.protectedPersist(mapper.toEntity(groupVM))),
                HttpStatus.OK);
    }

    @AutoPreAuthorize
    @PostMapping("/v1/find")
    public ResponseEntity<Page<GroupAdminVM>> findAll(@RequestBody SearchOption<GroupAdminVM> searchOption) {
        return new ResponseEntity<>(mapper.toViewModel(
                groupService.findAll(new SearchOption<Group>().from(searchOption, mapper))),
                HttpStatus.OK);
    }

    @AutoPreAuthorize
    @DeleteMapping("/v1/delete/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable @Param("id") long id) {
        groupService.deleteById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
