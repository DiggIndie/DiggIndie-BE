package ceos.diggindie.domain.member.dto;

import ceos.diggindie.domain.band.dto.BandListResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record BandPreferenceResponse(
        List<BandListResponse> bands
) {
    public static BandPreferenceResponse of(List<BandListResponse> bands) {
        return BandPreferenceResponse.builder()
                .bands(bands)
                .build();
    }
}
