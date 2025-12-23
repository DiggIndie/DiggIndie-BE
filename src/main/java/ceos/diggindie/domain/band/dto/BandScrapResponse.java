package ceos.diggindie.domain.band.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BandScrapResponse(
        List<BandListResponse> bands
) {
    public static BandScrapResponse of(List<BandListResponse> bands) {
        return BandScrapResponse.builder()
                .bands(bands)
                .build();
    }
}