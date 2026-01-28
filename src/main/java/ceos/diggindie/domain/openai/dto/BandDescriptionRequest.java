package ceos.diggindie.domain.openai.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "밴드 설명 생성 요청")
public record BandDescriptionRequest(
        @Schema(description = "시작 band_id (해당 ID 이후의 모든 밴드 처리)", example = "1")
        Long startBandId
) {
}
