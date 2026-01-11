package ceos.diggindie.domain.band.dto;

import lombok.Builder;

@Builder
public record TopTrackResponse(
        String title,
        String externalUrl
) {
}