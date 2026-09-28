package com.app.novastore.constants;

import java.net.URI;

public class
NovastoreConstants {

    /**
     * Constant <code>SPRING_PROFILE_DEVELOPMENT="dev"</code>
     */
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    /**
     * Constant <code>SPRING_PROFILE_TEST="test"</code>
     */
    public static final String SPRING_PROFILE_TEST = "test";

    /**
     * Constant <code>SPRING_PROFILE_STAGE="stage"</code>
     */
    public static final String SPRING_PROFILE_STAGE = "stage";

    /**
     * Constant <code>SPRING_PROFILE_PRODUCTION="prod"</code>
     */
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    /**
     * Constant <code>SPRING_PROFILE_PRODUCTION="easy-otp"</code>
     */
    public static final String SPRING_PROFILE_EASY_OTP = "easy-otp";


    public static final String SYSTEM = "system";

    /**
     * Authority that gates the operational endpoints (actuator). Granted to a small set of
     * operator accounts only - an ADMIN user does not get it by virtue of being an admin.
     */
    public static final String ROOT_AUTHORITY = "root";

    public static final String REDIS_HASH_PREFIX = "GOLDSTAR_HASH_";
    public static final URI DEFAULT_URL = URI.create("https://goldstar.bluebyte.ir/");

    /**
     * A key used in Spring State Machine
     */
    public static final String SSM_TENANT_CONFIG_KEY = "tenantConfigKey";

    public static final String SSM_TRIP_TYPE_KEY = "tripTypeKey";
    public static final String SSM_ENTRY_GATE_PASSED_KEY = "entryGatePassedKey";
    public static final String SSM_EXIT_GATE_PASSED_KEY = "exitGatePassedKey";
    public static final String SSM_RECEIPT_SCANNED_BY_DRIVER_KEY = "receiptScannedKey";
}
