package com.joinbe.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ADMIN_ACCOUNT = "admin";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String C_QUESTION_MARK = "?";
    public static final String C_VERTICAL_LINE = "|";
    public static final long VEHICLE_STOPPED_TIMELINE_MINUS = 3;

    public static final String ROLE_TYPE_MERCHANT = "M";

    public static final String CITY_ID_PREFIX = "C";
    public static final String SHOP_ID_PREFIX = "S";

    public static final String CITY_ROOT_ID = "C0";

    private Constants() {
    }
}
