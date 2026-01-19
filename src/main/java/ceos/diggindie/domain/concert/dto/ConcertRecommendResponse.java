package ceos.diggindie.domain.concert.dto;

import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.util.ConcertDDayCalculator;

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
                        ConcertDDayCalculator.calculate(concert.getStartDate()),
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
}
