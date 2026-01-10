package ceos.diggindie.domain.concert.dto;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record ConcertsListResponse(
        List<ConcertInfo> concerts,
        PageInfo pageInfo
) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public record ConcertInfo(
            Long concertId,
            String concertName,
            String dDay,
            List<String> lineUp,
            String mainImage,
            String period
    ) {
        public static ConcertInfo fromConcert(Concert concert) {
            // 라인업 추출 (BandConcert를 통해 밴드 이름 목록)
            List<String> lineUp = concert.getBandConcerts().stream()
                    .map(bc -> bc.getBand().getBandName())
                    .toList();

            // D-Day 계산
            String dDay = calculateDDay(concert.getStartDate());

            // 기간 포맷팅
            String period = formatPeriod(concert.getStartDate(), concert.getEndDate());

            return new ConcertInfo(
                    concert.getId(),
                    concert.getTitle(),
                    dDay,
                    lineUp,
                    concert.getMainImg(),
                    period
            );
        }

        private static String calculateDDay(LocalDateTime startDate) {
            LocalDate today = LocalDate.now();
            LocalDate concertDate = startDate.toLocalDate();
            long daysUntil = ChronoUnit.DAYS.between(today, concertDate);

            if (daysUntil < 0) {
                return "공연 종료";
            } else if (daysUntil == 0) {
                return "D-Day";
            } else {
                return "D-" + daysUntil;
            }
        }

        private static String formatPeriod(LocalDateTime startDate, LocalDateTime endDate) {
            String start = startDate.format(DATE_FORMATTER);
            if (endDate == null) {
                return start + " ~ " + start;
            }
            String end = endDate.format(DATE_FORMATTER);
            return start + " ~ " + end;
        }
    }

    public static ConcertsListResponse fromPagedConcerts(Page<Concert> concertPage) {
        List<ConcertInfo> concertInfos = concertPage.getContent().stream()
                .map(ConcertInfo::fromConcert)
                .toList();

        PageInfo pageInfo = new PageInfo(
                concertPage.getNumber(),
                concertPage.getSize(),
                concertPage.hasNext(),
                concertPage.getTotalElements(),
                concertPage.getTotalPages()
        );

        return new ConcertsListResponse(concertInfos, pageInfo);
    }
}
