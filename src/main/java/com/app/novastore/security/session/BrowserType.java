package com.app.novastore.security.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrowserType {

    CHROME(0, "Chrome", "کروم"),
    FIREFOX(1, "Firefox", "فایرفاکس"),
    OPERA(2, "Opera", "اپرا"),
    INTERNET_EXPLORER(3, "InternetExplorer", "اینترنت اکسپلورر"),
    MICROSOFT_EDGE(4, "MicrosoftEdge", "مایکروسافت ادج"),
    SAFARI(5, "Safari", "سافاری");

    private final Integer index;
    private final String title;
    private final String persianTitle;

    public static BrowserType valueOfIndex(Integer index) {
        for (BrowserType browserType : values()) {
            if (browserType.getIndex().equals(index)) {
                return browserType;
            }
        }
        throw new IllegalArgumentException("BrowserType cannot be resolved for code " + index);
    }

    public static BrowserType valueOfTitle(String title) {
        for (BrowserType browserType : values()) {
            if (browserType.getTitle().equalsIgnoreCase(title))
                return browserType;
        }
        return null;
    }

}
