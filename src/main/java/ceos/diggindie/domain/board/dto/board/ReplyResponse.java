package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import lombok.Builder;

@Builder
public record ReplyResponse(
        Long commentId,
        Long parentCommentId,
        String writerNickname,
        String replyToNickname,
        Boolean replyToIsAnonymous,
        String createdAt,
        String content,
        Boolean isAnonymous,
        Integer likeCount,
        Boolean isLiked,
        Integer depth
) {
    public static ReplyResponse of(BoardComment comment, int depth, Long memberId) {
        BoardComment parent = comment.getParentComment();
        String replyTo = null;
        Boolean replyToAnonymous = null;

        if (parent != null) {
            replyToAnonymous = parent.getIsAnonymous();
            replyTo = parent.getMember().getUserId();
        }

        boolean liked = comment.getLikes().stream()
                .anyMatch(like -> like.getMember().getId().equals(memberId));

        return ReplyResponse.builder()
                .commentId(comment.getId())
                .parentCommentId(parent != null ? parent.getId() : null)
                .writerNickname(comment.getMember().getUserId())
                .replyToNickname(replyTo)
                .replyToIsAnonymous(replyToAnonymous)
                .createdAt(TimeUtils.toRelativeTime(comment.getCreatedAt()))
                .content(comment.getContent())
                .isAnonymous(comment.getIsAnonymous())
                .likeCount(comment.getLikes().size())
                .isLiked(liked)
                .depth(Math.min(depth, 2))
                .build();
    }
}