package com.app.novastore.security.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperatingSystemType {

    WINDOWS(0, "windows", "ویندوز"),
    LINUX(1, "linux", "لینوکس"),
    ANDROID(2, "android", "اندروید"),
    IOS(3, "iOs", "آی او اس"),
    MAC_OS(4, "macOs", "مک او اس"),
    MAC_OSX(5, "macOsx", "مک او اس ایکس");

    private final Integer index;
    private final String title;
    private final String persianTitle;

    public static OperatingSystemType valueOfIndex(Integer index) {
        for (OperatingSystemType operatingSystemType : values()) {
            if (operatingSystemType.getIndex().equals(index)) {
                return operatingSystemType;
            }
        }
        throw new IllegalArgumentException("OperatingSystemType cannot be resolved for code " + index);
    }

    public static OperatingSystemType valueOfTitle(String title) {
        for (OperatingSystemType operatingSystemType : values()) {
            if (operatingSystemType.getTitle().equalsIgnoreCase(title))
                return operatingSystemType;
        }
        return null;
    }
}
