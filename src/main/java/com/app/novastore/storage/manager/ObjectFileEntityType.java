package com.app.novastore.storage.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ObjectFileEntityType {

    USERS       ("USERS"    , 800, 800, 400, 0.80f),
    DRIVERS     ("DRIVERS"  , 800, 800, 400, 0.80f),
    TENANTS     ("TENANTS"  , 800, 800, 400, 0.80f),
    ICONS       ("ICONS"    , 320, 180, 100, 0.75f),
    ;
    private final String name;
    private final int maxWidth;
    private final int maxHeight;
    private final int targetKB;
    private final float quality;
}
