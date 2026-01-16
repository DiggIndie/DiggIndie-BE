package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import lombok.Builder;

import java.util.List;

@Builder
public record CommentResponse(
        Long commentId,
        String writerNickname,
        String createdAt,
        String content,
        Boolean isAnonymous,
        Integer likeCount,
        List<CommentResponse> childComments
) {
    public static CommentResponse from(BoardComment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .writerNickname(comment.getIsAnonymous() ? "익명" : comment.getMember().getUserId())
                .createdAt(TimeUtils.toRelativeTime(comment.getCreatedAt()))
                .content(comment.getContent())
                .isAnonymous(comment.getIsAnonymous())
                .likeCount(comment.getLikes().size())
                .childComments(comment.getChildComments().stream()
                        .map(CommentResponse::from)
                        .toList())
                .build();
    }
}