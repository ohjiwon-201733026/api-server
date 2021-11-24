package com.gloomy.server.core.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {

    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }

    public static String getLocalDateTimeNowStringPattern(String pattern) {
        return getLocalDateTimeNow().format(DateTimeFormatter.ofPattern(pattern));
    }
}
