package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.utils.AnonymousNumberGenerator;
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
        Integer likeCount,
        Boolean isLiked,
        Boolean isMine,
        List<ReplyResponse> replies
) {
    // 상세 조회용
    public static CommentResponse from(BoardComment comment, Long memberId,
                                       AnonymousNumberGenerator anonGenerator) {
        List<ReplyResponse> allReplies = new ArrayList<>();
        collectReplies(comment.getChildComments(), allReplies, 1, memberId, anonGenerator);

        boolean liked = comment.getLikes().stream()
                .anyMatch(like -> like.getMember().getId().equals(memberId));

        boolean isMine = memberId != null && comment.getMember().getId().equals(memberId);

        String writerNick = anonGenerator.getNickname(
                comment.getMember().getId(),
                comment.getIsAnonymous(),
                comment.getMember().getUserId()
        );

        return CommentResponse.builder()
                .commentId(comment.getId())
                .writerNickname(writerNick)
                .createdAt(TimeUtils.toRelativeTime(comment.getCreatedAt()))
                .content(comment.getContent())
                .likeCount(comment.getLikes().size())
                .isLiked(liked)
                .isMine(isMine)
                .replies(allReplies)
                .build();
    }

    // 댓글 작성 응답용
    public static CommentResponse from(BoardComment comment, Long memberId) {
        String writerNick = comment.getIsAnonymous()
                ? "익명"
                : comment.getMember().getUserId();

        boolean isMine = memberId != null && comment.getMember().getId().equals(memberId);

        return CommentResponse.builder()
                .commentId(comment.getId())
                .writerNickname(writerNick)
                .createdAt(TimeUtils.toRelativeTime(comment.getCreatedAt()))
                .content(comment.getContent())
                .likeCount(0)
                .isLiked(false)
                .isMine(isMine)
                .replies(new ArrayList<>())
                .build();
    }

    private static void collectReplies(List<BoardComment> comments, List<ReplyResponse> result,
                                       int depth, Long memberId,
                                       AnonymousNumberGenerator anonGenerator) {
        for (BoardComment comment : comments) {
            result.add(ReplyResponse.of(comment, depth, memberId, anonGenerator));
            collectReplies(comment.getChildComments(), result, depth + 1, memberId, anonGenerator);
        }
    }
}