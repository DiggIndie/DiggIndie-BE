package ceos.diggindie.domain.band.dto;

import ceos.diggindie.domain.concert.dto.ConcertSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

@Builder
@Schema(description = "아티스트 상세 정보")
public record BandDetailResponse(
        @Schema(description = "아티스트 ID", example = "12")
        Long artistId,

        @Schema(description = "아티스트명", example = "검정치마")
        String artistName,

        @Schema(description = "키워드 목록", example = "[\"인디\", \"락\", \"감성\"]")
        List<String> keywords,

        @Schema(description = "아티스트 이미지 URL", example = "https://i.scdn.co/image/xxx")
        String artistImage,

        @Schema(description = "아티스트 소개", example = "2004년 결성된 인디 록 밴드")
        String description,

        @Schema(description = "멤버 이름 목록", example = "[\"조휴일\", \"김예지\"]")
        List<String> members,

        @Schema(description = "대표곡 정보")
        TopTrackResponse topTrack,

        @Schema(description = "앨범 목록 (최신순)")
        List<AlbumResponse> albums,

        @Schema(description = "예정 공연 목록 (D-Day 포함)")
        List<ConcertSummaryResponse> scheduledConcerts,

        @Schema(description = "완료 공연 목록")
        List<ConcertSummaryResponse> endedConcerts,

        @Schema(description = "스크랩 여부 (비로그인 시 false)", example = "true")
        boolean isScraped
) {
}