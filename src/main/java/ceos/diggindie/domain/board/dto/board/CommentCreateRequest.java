package ceos.diggindie.domain.board.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content,

        @NotNull(message = "익명 여부를 선택해주세요.")
        Boolean isAnonymous,

        Long parentCommentId
) {}