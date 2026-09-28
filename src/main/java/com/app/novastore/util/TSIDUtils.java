package com.app.novastore.util;

import io.hypersistence.tsid.TSID;

public final class TSIDUtils {

    public static synchronized String randomString() {
        return String.valueOf(TSID.fast());
    }

    public static synchronized Long randomNumber() {
        return TSID.fast().toLong();
    }
}
