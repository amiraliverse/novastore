package com.app.novastore.storage.minio;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.exception.ApplicationException;
import com.app.novastore.storage.manager.ObjectFileEntityType;
import com.app.novastore.storage.manager.ObjectStorageFile;
import com.app.novastore.storage.manager.ObjectStorageManager;
import com.app.novastore.util.ImageCompressorUtil;
import com.app.novastore.util.UserFileUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class MinioService {

    private final ObjectStorageManager storageManager;
    private final NovastoreProperties properties;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    public ObjectStorageFile upload(MultipartFile file, ObjectFileEntityType type) {
        String contentType = file.getContentType();

        if (!StringUtils.hasText(file.getOriginalFilename())) {
            throw new ApplicationException("file extension not found", "فایل دارای فرمت مشخصی نمیباشد", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        }

        String fileExtension = UserFileUtils.getFileExtension(file.getOriginalFilename());
        if (contentType == null
            || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())
            || !ALLOWED_FILE_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new ApplicationException("file extension not allowed", "فرمت فایل پشتیبانی نمیشود", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        }

        ObjectStorageFile result = null;
        File tempFile = null;

        try {
            tempFile = File.createTempFile("upload_", "_" + file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
        } catch (Exception fallbackEx) {
            log.error("MinIO operation failed", fallbackEx);
            throw new RuntimeException(fallbackEx);
        }

        try {
            tempFile = ImageCompressorUtil.compressImage(tempFile, type);
        } catch (Exception compressionEx) {
            log.error("MinIO operation failed", compressionEx);
        }

        try {
            if (tempFile != null && tempFile.exists()) {
                String fileName = getFileName(type, UserFileUtils.getFileExtension(tempFile.getAbsolutePath()));
                result = storageManager.uploadFile(fileName, tempFile);
            }
        } catch (Exception uploadEx) {
            log.error("MinIO operation failed", uploadEx);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                } catch (Exception cleanupEx) {
                }
            }
        }

        return result;
    }


    private String getFileName(ObjectFileEntityType type, String fileExtension) {
        return properties.getObjectStorage().getMinio().getPath().getImages()
                            .concat("/")
                            .concat(type.getName().toLowerCase()) + "/" + UserFileUtils.getFileName(fileExtension);
    }
}
