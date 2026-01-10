package ceos.diggindie.domain.spotify.dto;

public record TopTrackUpdateAllResponse(
        int totalCount,
        int successCount,
        int failCount
) {
}

