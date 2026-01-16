package ceos.diggindie.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

    public static String toRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분 전";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "시간 전";
        } else if (seconds < 2592000) {
            return (seconds / 86400) + "일 전";
        } else if (seconds < 31536000) {
            return (seconds / 2592000) + "개월 전";
        } else {
            return (seconds / 31536000) + "년 전";
        }
    }
}