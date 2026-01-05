package ceos.diggindie.domain.band.dto;

import lombok.Builder;

import java.util.List;

public class BandScrapResponse {

    public record BandScrapPageResponse(
            List<BandScrapInfoDTO> scraps
    ) {}

    @Builder
    public record BandScrapInfoDTO(
            Long bandId,
            String bandName,
            List<String> keywords,
            String bandImage,
            String mainMusic
    ) {}
}