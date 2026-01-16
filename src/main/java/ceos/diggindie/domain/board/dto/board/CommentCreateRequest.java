package ceos.diggindie.domain.board.dto.board;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content,

        Long parentCommentId  // null이면 일반 댓글, 값 있으면 대댓글
) {}