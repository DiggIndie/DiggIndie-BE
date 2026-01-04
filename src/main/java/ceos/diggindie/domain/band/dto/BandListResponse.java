package ceos.diggindie.domain.band.dto;

import lombok.Builder;

@Builder
public record BandListResponse(
        Long bandId,
        String imageUrl,
        String bandName
) {
    public static BandListResponse from(ceos.diggindie.domain.band.entity.Band band) {
        return BandListResponse.builder()
                .bandId(band.getId())
                .imageUrl(band.getMainImage())
                .bandName(band.getBandName())
                .build();
    }
}