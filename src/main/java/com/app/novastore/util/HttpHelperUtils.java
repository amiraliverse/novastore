package com.app.novastore.util;

import com.app.novastore.security.session.BrowserType;
import com.app.novastore.security.session.OperatingSystemType;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public final class HttpHelperUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };


    public static Optional<HttpServletRequest> getRequestIfPossible() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes).getRequest());
    }

    public static Optional<HttpServletResponse> getResponseIfPossible() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes).getResponse());
    }

    public static String getClientIpAddressIfServletRequestExist() {
        return getRequestIfPossible()
                .map(request -> {
                    for (String header : IP_HEADER_CANDIDATES) {
                        String ipList = request.getHeader(header);
                        if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
                            return ipList.split(",")[0];
                        }
                    }

                    return request.getRemoteAddr();
                })
                .orElse("0.0.0.0");

    }

    public static ClientDeviceSpec getClientDeviceSpecifications() {
        return getRequestIfPossible()
                .map(request -> {
                    UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
                    Browser browser = userAgent.getBrowser();
                    String requestBrowser = browser.getGroup().getName().replace(" ", "");
                    OperatingSystem operatingSystem = userAgent.getOperatingSystem();
                    String requestOperatingSystem = operatingSystem.getGroup().getName().replace(" ", "");

                    return new ClientDeviceSpec(operatingSystem.getName(),
                            OperatingSystemType.valueOfTitle(requestOperatingSystem),
                            browser.getName(),
                            BrowserType.valueOfTitle(requestBrowser));
                })
                .orElse(new ClientDeviceSpec(null, null, null, null));

    }

    public record ClientDeviceSpec(String operatingSystemName, OperatingSystemType operatingSystemType,
                                   String browserName, BrowserType browserType) {
    }
}
