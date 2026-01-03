package ceos.diggindie.domain.concert.dto;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public record ConcertWeeklyCalendarResponse(
        List<ConcertInfo> concerts,
        PageInfo pageInfo
) {
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("a h:mm", Locale.KOREAN);

    public record ConcertInfo(
            Long concertId,
            String concertName,
            String startsAt,
            String concertHall
    ) {
        public static ConcertInfo fromConcert(Concert concert) {
            return new ConcertInfo(
                    concert.getId(),
                    concert.getTitle(),
                    concert.getStartDate().format(TIME_FORMATTER),
                    concert.getConcertHall().getName()
            );
        }
    }

    public static ConcertWeeklyCalendarResponse fromPagedConcerts(
            Page<Concert> concertPage
    ) {

        List<ConcertInfo> concertInfos = concertPage.stream()
                .map(ConcertWeeklyCalendarResponse.ConcertInfo::fromConcert)
                .toList();

        PageInfo pageInfo = new PageInfo(
                concertPage.getNumber(),
                concertPage.getSize(),
                concertPage.hasNext(),
                concertPage.getTotalElements(),
                concertPage.getTotalPages()
        );

        return new ConcertWeeklyCalendarResponse(concertInfos, pageInfo);
    }
}
