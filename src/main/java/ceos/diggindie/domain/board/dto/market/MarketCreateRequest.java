package ceos.diggindie.domain.board.dto.market;

import ceos.diggindie.common.enums.MarketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MarketCreateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content,

        @NotNull(message = "가격은 필수입니다.")
        Integer price,

        String chatUrl,

        @NotNull(message = "타입은 필수입니다.")
        MarketType type,

        List<String> imageUrls
) {
}