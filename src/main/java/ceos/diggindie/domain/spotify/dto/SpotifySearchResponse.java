package ceos.diggindie.domain.spotify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SpotifySearchResponse(
        Artists artists
) {
    public record Artists(
            String href,
            int limit,
            String next,
            int offset,
            String previous,
            int total,
            List<Artist> items
    ) {}

    public record Artist(
            ExternalUrls external_urls,
            Followers followers,
            List<String> genres,
            String href,
            String id,
            List<Image> images,
            String name,
            int popularity,
            String type,
            String uri
    ) {}

    public record ExternalUrls(
            String spotify
    ) {}

    public record Followers(
            String href,
            int total
    ) {}

    public record Image(
            String url,
            int height,
            int width
    ) {}
}
