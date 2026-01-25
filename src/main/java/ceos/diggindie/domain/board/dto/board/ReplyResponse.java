package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.utils.AnonymousNumberGenerator;
import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import lombok.Builder;

@Builder
public record ReplyResponse(
        Long commentId,
        Long parentCommentId,
        String writerNickname,
        String replyToNickname,
        String createdAt,
        String content,
        Integer likeCount,
        Boolean isLiked,
        Integer depth
) {
    public static ReplyResponse of(BoardComment comment, int depth, Long memberId,
                                   AnonymousNumberGenerator anonGenerator) {
        BoardComment parent = comment.getParentComment();
        String replyTo = null;

        if (parent != null) {
            replyTo = anonGenerator.getNickname(
                    parent.getMember().getId(),
                    parent.getIsAnonymous(),
                    parent.getMember().getUserId()
            );
        }

        boolean liked = comment.getLikes().stream()
                .anyMatch(like -> like.getMember().getId().equals(memberId));

        String writerNick = anonGenerator.getNickname(
                comment.getMember().getId(),
                comment.getIsAnonymous(),
                comment.getMember().getUserId()
        );

        return ReplyResponse.builder()
                .commentId(comment.getId())
                .parentCommentId(parent != null ? parent.getId() : null)
                .writerNickname(writerNick)
                .replyToNickname(replyTo)
                .createdAt(TimeUtils.toRelativeTime(comment.getCreatedAt()))
                .content(comment.getContent())
                .likeCount(comment.getLikes().size())
                .isLiked(liked)
                .depth(Math.min(depth, 2))
                .build();
    }
}