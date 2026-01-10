package ceos.diggindie.domain.spotify.dto;

import java.util.List;

public record SpotifyTopTrackResponse(
        List<TrackDto> tracks
) {

    public record TrackDto(
            String name,
            ExternalUrlsDto external_urls
    ) {}

    public record ExternalUrlsDto(
            String spotify
    ) {}
}
