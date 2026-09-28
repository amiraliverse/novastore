package com.app.novastore.security.session.vm;

import com.app.novastore.security.session.BrowserType;
import com.app.novastore.security.session.OperatingSystemType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SessionVM {
    private String id;
    private String userId;
    private String ip;
    private BrowserType browserType;
    private String browserName;
    private OperatingSystemType operatingSystemType;
    private String operatingSystemName;
    private Long expiredIn;
    private Boolean isExpired;
    private Boolean isCurrentDevice;
    private String tokenType;
    private Date date;

}
