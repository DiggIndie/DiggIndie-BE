package ceos.diggindie.domain.band.dto;

import ceos.diggindie.domain.concert.dto.ConcertSummaryResponse;
import lombok.Builder;
import java.util.List;

@Builder
public record BandDetailResponse(
        Long artistId,
        String artistName,
        List<String> keywords,
        String artistImage,
        String description,
        List<String> members,
        TopTrackResponse topTrack,
        List<AlbumResponse> albums,
        List<ConcertSummaryResponse> scheduledConcerts,
        List<ConcertSummaryResponse> endedConcerts,
        boolean isScraped
) {
}