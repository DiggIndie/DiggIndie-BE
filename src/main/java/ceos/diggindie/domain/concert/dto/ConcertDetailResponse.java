package ceos.diggindie.domain.concert.dto;

import ceos.diggindie.domain.concert.entity.Concert;

import java.time.LocalDateTime;
import java.util.List;

public record ConcertDetailResponse(
        String concertName,
        boolean isScrapped,
        LocalDateTime startDate,
        String concertHallName,
        String address,
        Integer preorderPrice,
        Integer onsitePrice,
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

        return new ConcertDetailResponse(
                concert.getTitle(),
                isScrapped,
                concert.getStartDate(),
                concert.getConcertHall().getName(),
                concert.getConcertHall().getAddress(),
                concert.getPreorderPrice(),
                concert.getOnSitePrice(),
                concert.getMainImg(),
                concert.getDescription(),
                lineUpInfos
        );
    }
}
