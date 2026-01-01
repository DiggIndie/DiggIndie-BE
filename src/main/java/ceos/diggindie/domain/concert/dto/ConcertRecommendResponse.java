package ceos.diggindie.domain.concert.dto;

import ceos.diggindie.domain.concert.entity.Concert;

import java.time.LocalDate;
import java.util.List;

public record ConcertRecommendResponse(
    List<ConcertInfo> concerts
) {
    public record ConcertInfo(
        Long concertId,
        String concertName,
        String duration,
        String dDay,
        String imageUrl,
        List<LineUp> lineUp
    ) {}

    public record LineUp(
        Long bandId,
        String bandName
    ) {}

    public static ConcertRecommendResponse fromConcerts(List<Concert> concerts) {
        List<ConcertInfo> concertInfos = concerts.stream()
                .map(concert -> new ConcertInfo(
                        concert.getId(),
                        concert.getTitle(),
                        formatDuration(concert),
                        calculateDDay(concert),
                        concert.getMainImg(),
                        concert.getBandConcerts().stream()
                                .map(bc -> new LineUp(
                                        bc.getBand().getId(),
                                        bc.getBand().getBandName()
                                ))
                                .toList()
                ))
                .toList();
        return new ConcertRecommendResponse(concertInfos);
    }

    private static String formatDuration(Concert concert) {
        if (concert.getStartDate() == null || concert.getEndDate() == null) return "";
        LocalDate start = concert.getStartDate().toLocalDate();
        LocalDate end = concert.getEndDate().toLocalDate();
        if (start.getYear() == end.getYear() && start.getMonth() == end.getMonth()) {
            return String.format("%d.%d.%d ~ %d", start.getYear(), start.getMonthValue(), start.getDayOfMonth(), end.getDayOfMonth());
        } else if (start.getYear() == end.getYear()) {
            return String.format("%d.%d.%d ~ %d.%d", start.getYear(), start.getMonthValue(), start.getDayOfMonth(), end.getMonthValue(), end.getDayOfMonth());
        } else {
            return String.format("%d.%d.%d ~ %d.%d.%d", start.getYear(), start.getMonthValue(), start.getDayOfMonth(), end.getYear(), end.getMonthValue(), end.getDayOfMonth());
        }
    }

    private static String calculateDDay(Concert concert) {
        if (concert.getStartDate() == null) return "";
        LocalDate today = java.time.LocalDate.now();
        LocalDate start = concert.getStartDate().toLocalDate();
        long days = java.time.temporal.ChronoUnit.DAYS.between(today, start);
        if (days > 0) {
            return "D-" + days;
        } else if (days == 0) {
            return "D-DAY";
        } else {
            return "D+" + Math.abs(days);
        }
    }
}
