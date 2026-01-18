package ceos.diggindie.domain.concert.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
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

    @Getter
    @Builder
    public static class ConcertScrapCreateDTO {
        private String memberId;
        private Long concertId;
    }

    @Getter
    @Builder
    public static class ConcertScrapToggleDTO {
        private String memberId;
        private Long concertId;

        @Getter(AccessLevel.NONE)
        private boolean isScrapped;

        @JsonProperty("isScrapped")
        public boolean isScrapped() {
            return isScrapped;
        }
    }
}