package ceos.diggindie.domain.concert.dto;

import ceos.diggindie.domain.concert.entity.Concert;

import java.time.LocalDateTime;
import java.util.List;

public record ConcertDetailResponse(
        Long concertId,
        String concertName,
        boolean isScrapped,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isFinished,
        String concertHallName,
        String address,
        Integer preorderPrice,
        Integer onsitePrice,
        String bookUrl,
        String imageUrl,
        String description,
        List<LineUpInfo> lineUp
) {
    public record LineUpInfo(
            Long bandId,
            String bandName,
            String bandImage
    ) {}

    public static ConcertDetailResponse fromConcert(Concert concert, boolean isScrapped) {
        List<LineUpInfo> lineUpInfos = concert.getBandConcerts().stream()
                .map(bc -> new LineUpInfo(
                        bc.getBand().getId(),
                        bc.getBand().getBandName(),
                        bc.getBand().getMainImage()
                ))
                .toList();

        // 공연 종료 여부 판단: endDate가 현재 시간보다 이전인 경우
        LocalDateTime endDate = concert.getEndDate() != null ? concert.getEndDate() : concert.getStartDate();
        boolean isFinished = endDate.isBefore(LocalDateTime.now());

        return new ConcertDetailResponse(
                concert.getId(),
                concert.getTitle(),
                isScrapped,
                concert.getStartDate(),
                concert.getEndDate(),
                isFinished,
                concert.getConcertHall().getName(),
                concert.getConcertHall().getAddress(),
                concert.getPreorderPrice(),
                concert.getOnSitePrice(),
                concert.getBookUrl(),
                concert.getMainImg(),
                concert.getDescription(),
                lineUpInfos
        );
    }
}
