package ceos.diggindie.domain.spotify.dto;

import java.util.List;

public record SpotifyAlbumsResponse(
        String href,
        int limit,
        String next,
        int offset,
        int total,
        List<AlbumItem> items
) {
    public record AlbumItem(
            String id,
            String name,
            String album_type,
            String release_date,
            int total_tracks,
            List<Image> images,
            ExternalUrls external_urls
    ) {}

    public record Image(
            String url,
            int height,
            int width
    ) {}

    public record ExternalUrls(
            String spotify
    ) {}
}