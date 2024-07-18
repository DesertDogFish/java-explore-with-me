package ru.practicum.explorewithme.ewmservice.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
