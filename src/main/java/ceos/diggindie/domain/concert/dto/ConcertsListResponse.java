package ceos.diggindie.domain.concert.dto;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.util.ConcertDDayCalculator;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            String dDay = ConcertDDayCalculator.calculate(concert.getStartDate());

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


        private static String formatPeriod(LocalDateTime startDate, LocalDateTime endDate) {
            LocalDate start = startDate.toLocalDate();
            LocalDate end = (endDate != null) ? endDate.toLocalDate() : start;

            // 하루짜리 공연
            if (start.equals(end)) {
                return start.format(DATE_FORMATTER);
            }

            // 같은 년/월인 경우: "2025.12.11 ~ 17"
            if (start.getYear() == end.getYear() && start.getMonth() == end.getMonth()) {
                return start.format(DATE_FORMATTER) + " ~ " + end.getDayOfMonth();
            }

            // 같은 년도인 경우: "2025.12.11 ~ 01.05"
            if (start.getYear() == end.getYear()) {
                return start.format(DATE_FORMATTER) + " ~ " + end.format(DateTimeFormatter.ofPattern("MM.dd"));
            }

            // 다른 년도인 경우: "2025.12.31 ~ 2026.01.01"
            return start.format(DATE_FORMATTER) + " ~ " + end.format(DATE_FORMATTER);
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
