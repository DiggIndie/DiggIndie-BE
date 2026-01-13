package ceos.diggindie.domain.concert.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ConcertScrapResponse {

    @Getter
    @Builder
    public static class ConcertScrapListDTO {
        private List<ConcertScrapInfoDTO> concerts;
    }

    @Getter
    @Builder
    public static class ConcertScrapInfoDTO {
        private Long concertId;
        private String concertName;
        private String duration;
        private String dDay;
        private String imageUrl;
        private boolean isFinished;
    }
}