package ceos.diggindie.domain.band.dto;

import lombok.Builder;

@Builder
public record AlbumResponse(
        Long albumId,
        String albumName,
        String albumImage,
        String releaseYear
) {
}