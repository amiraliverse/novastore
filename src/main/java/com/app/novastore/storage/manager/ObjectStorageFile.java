package com.app.novastore.storage.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ObjectStorageFile {
    private String url;
    private String fileName;
    private long fileSize;
}
