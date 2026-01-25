package ceos.diggindie.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String toRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        }

        else if (seconds < 3600) {
            return (seconds / 60) + "분 전";
        }

        else if (seconds < 86400) {
            return (seconds / 3600) + "시간 전";
        }

        else {
            return dateTime.format(DATE_FORMATTER);
        }
    }
}