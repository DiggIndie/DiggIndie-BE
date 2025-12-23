package ceos.diggindie.domain.band.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class BandScrapResponse {

    @Getter
    @Builder
    public static class BandScrapPageResponse {
        private List<BandScrapInfoDTO> scraps;
    }

    @Getter
    @Builder
    public static class BandScrapInfoDTO {
        private Long bandId;
        private String bandName;
        private List<String> keywords;
        private String bandImage;
        private String mainMusic;
    }
}