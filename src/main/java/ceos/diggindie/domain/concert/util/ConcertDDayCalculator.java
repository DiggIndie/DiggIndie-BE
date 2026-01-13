package ceos.diggindie.domain.concert.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 공연 D-Day 계산 유틸리티
 */
public class ConcertDDayCalculator {

    private ConcertDDayCalculator() {
        throw new IllegalStateException("Utility class");
    }


    public static String calculate(LocalDate startDate, LocalDate endDate, boolean showPastDays) {
        if (startDate == null) {
            return "";
        }

        LocalDate today = LocalDate.now();
        LocalDate end = (endDate != null) ? endDate : startDate;

        if (today.isBefore(end)) { // 공연 종료 전
            long days = ChronoUnit.DAYS.between(today, startDate);
            return days == 0 ? "D-Day" : "D-" + days;
        } else {
            if (showPastDays) { // 공연 종료 후
                long days = ChronoUnit.DAYS.between(startDate, today);
                return "D+" + days;
            }
            return endDate != null ? "종료" : "공연 종료";
        }
    }


    public static String calculate(LocalDateTime startDate) {
        return startDate != null ? calculate(startDate.toLocalDate(), null, false) : "";
    }


    public static String calculate(LocalDate startDate, LocalDate endDate) {
        return calculate(startDate, endDate, false);
    }


    public static String calculateWithPastDays(LocalDateTime startDate) {
        return startDate != null ? calculate(startDate.toLocalDate(), null, true) : "";
    }
}

