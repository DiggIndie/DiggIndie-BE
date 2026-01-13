package ceos.diggindie.domain.concert.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ConcertMonthlyCalendarResponse(
        int year,
        int month,
        List<DayInfo> days
) {
    public record DayInfo(
            int day,
            boolean hasConcert
    ) {}

    public static ConcertMonthlyCalendarResponse from(int year, int month, Set<LocalDate> concertDates) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        List<DayInfo> dayInfos = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            boolean hasConcert = concertDates.contains(date);
            dayInfos.add(new DayInfo(day, hasConcert));
        }

        return new ConcertMonthlyCalendarResponse(year, month, dayInfos);
    }
}
