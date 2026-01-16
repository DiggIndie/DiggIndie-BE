package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record CommentResponse(
        Long commentId,
        String writerNickname,
        String createdAt,
        String content,
        Boolean isAnonymous,
        Integer likeCount,
        Boolean isLiked,
        List<ReplyResponse> replies
) {
    public static CommentResponse from(BoardComment comment, Long memberId) {
        List<ReplyResponse> allReplies = new ArrayList<>();
        collectReplies(comment.getChildComments(), allReplies, 1, memberId);

        boolean liked = comment.getLikes().stream()
                .anyMatch(like -> like.getMember().getId().equals(memberId));

        return CommentResponse.builder()
                .commentId(comment.getId())
                .writerNickname(comment.getMember().getUserId())
                .createdAt(TimeUtils.toRelativeTime(comment.getCreatedAt()))
                .content(comment.getContent())
                .isAnonymous(comment.getIsAnonymous())
                .likeCount(comment.getLikes().size())
                .isLiked(liked)
                .replies(allReplies)
                .build();
    }

    private static void collectReplies(List<BoardComment> comments, List<ReplyResponse> result,
                                       int depth, Long memberId) {
        for (BoardComment comment : comments) {
            result.add(ReplyResponse.of(comment, depth, memberId));
            collectReplies(comment.getChildComments(), result, depth + 1, memberId);
        }
    }
}