package com.app.novastore.storage.manager;

import java.io.File;
import java.io.InputStream;

public interface ObjectStorageManager {
    /**
     * Uploads a file to the storage.
     * @param fileName The name of the file.
     * @param file The file.
     * @return The URL, fileName, fileSize.
     */
    ObjectStorageFile uploadFile(String fileName, File file);

    /**
     * Downloads a file from the storage.
     * @param fileName The name of the file.
     * @return The file content as InputStream.
     */
    InputStream downloadFile(String fileName);

    /**
     * Deletes a file from the storage.
     * @param fileName The name of the file.
     */
    void deleteFile(String fileName);

    /**
     * Checks if a file exists in the storage.
     * @param fileName The name of the file.
     * @return True if the file exists, false otherwise.
     */
    boolean fileExists(String fileName);
}
