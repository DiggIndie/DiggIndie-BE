package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.enums.BoardCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BoardCreateRequest(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        @NotNull(message = "익명 여부를 선택해주세요.")
        Boolean isAnonymous,

        @NotNull(message = "카테고리를 선택해주세요.")
        BoardCategory category,

        List<String> imageUrls
) {
    public BoardCreateRequest {
        if (imageUrls == null) {
            imageUrls = List.of();
        }
    }
}