package ceos.diggindie.domain.band.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "대표곡 정보")
public record TopTrackResponse(
        @Schema(description = "곡 제목", example = "Sunset")
        String title,

        @Schema(description = "Spotify 외부 링크", example = "https://open.spotify.com/track/xxx")
        String externalUrl
) {
}