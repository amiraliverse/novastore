package com.app.novastore.storage.minio;

import com.app.novastore.annotations.auth.AutoPreAuthorize;
import com.app.novastore.storage.manager.ObjectFileEntityType;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/storage")
public class MinioResource {

    private final MinioService minioService;

    @AutoPreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/v1/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                                        @RequestParam("type") ObjectFileEntityType type) {
        return ResponseEntity.ok(minioService.upload(file, type).getUrl());
    }
}
