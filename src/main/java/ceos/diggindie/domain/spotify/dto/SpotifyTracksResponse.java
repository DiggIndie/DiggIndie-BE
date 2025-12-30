package ceos.diggindie.domain.spotify.dto;

import java.util.List;

public record SpotifyTracksResponse(
        String href,
        int limit,
        String next,
        int offset,
        int total,
        List<TrackItem> items
) {
    public record TrackItem(
            String id,
            String name,
            int track_number,
            int duration_ms,
            String preview_url,
            ExternalUrls external_urls
    ) {}

    public record ExternalUrls(
            String spotify
    ) {}
}