package com.app.novastore.storage.minio;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.storage.manager.ObjectStorageFile;
import com.app.novastore.storage.manager.ObjectStorageManager;
import io.minio.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

@Slf4j
@Service
@AllArgsConstructor
public class MinioStorageManager implements ObjectStorageManager {
    private final MinioClient minioClient;
    private final NovastoreProperties properties;

    @Override
    public ObjectStorageFile uploadFile(String fileName, File file) {
        if (file == null || !file.exists() || file.length() == 0) {
            throw new IllegalArgumentException("File is null, doesn't exist, or is empty.");
        }

        long fileSize = file.length();
        String bucketName = properties.getObjectStorage().getMinio().getBucketName();

        log.info("Uploading file '{}' to bucket '{}'", fileName, bucketName);

        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, fileSize, 5242880L) // 5MB partSize
                            .contentType(Files.probeContentType(file.toPath()))
                            .build()
            );
            log.info("File '{}' uploaded successfully!", fileName);
            inputStream.close();
            return new ObjectStorageFile(getFullFileName(fileName), getOnlyFileName(fileName), fileSize);
        } catch (Exception e) {
            log.error("Error uploading file '{}' to MinIO", fileName, e);
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }

    @Override
    public InputStream downloadFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getObjectStorage().getMinio().getBucketName())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from MinIO", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getObjectStorage().getMinio().getBucketName())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from MinIO", e);
        }
    }

    @Override
    public boolean fileExists(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getObjectStorage().getMinio().getBucketName())
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getFullFileName(String fileName) {
        return properties.getObjectStorage().getMinio().getPath().getEndPointUrl() + "/"
                + properties.getObjectStorage().getMinio().getBucketName()
                + fileName;
    }

    private String getOnlyFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("/") + 1);
    }

}
