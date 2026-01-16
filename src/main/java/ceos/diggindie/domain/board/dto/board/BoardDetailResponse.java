package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.utils.AnonymousNumberGenerator;
import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import lombok.Builder;

import java.util.List;
@Builder
public record BoardDetailResponse(
        Long boardId,
        String category,
        String title,
        String writerNickname,
        String createdAt,
        String content,
        List<String> imageUrls,
        Integer views,
        Integer likeCount,
        Boolean isLiked,
        Integer commentCount,
        List<CommentResponse> comments
) {
    public static BoardDetailResponse of(Board board, List<BoardComment> comments, Long memberId) {
        int totalCommentCount = countAllComments(comments);

        boolean liked = board.getBoardLikes().stream()
                .anyMatch(like -> like.getMember().getId().equals(memberId));

        AnonymousNumberGenerator anonGenerator = new AnonymousNumberGenerator(board, comments);

        String writerNick = anonGenerator.getNickname(
                board.getMember().getId(),
                board.getIsAnonymous(),
                board.getMember().getUserId()
        );

        return BoardDetailResponse.builder()
                .boardId(board.getId())
                .category(board.getCategory().getDescription())
                .title(board.getTitle())
                .writerNickname(writerNick)
                .createdAt(TimeUtils.toRelativeTime(board.getCreatedAt()))
                .content(board.getContent())
                .imageUrls(board.getBoardImages().stream()
                        .map(img -> img.getImageUrl())
                        .toList())
                .views(board.getViews())
                .likeCount(board.getBoardLikes().size())
                .isLiked(liked)
                .commentCount(totalCommentCount)
                .comments(comments.stream()
                        .map(c -> CommentResponse.from(c, memberId, anonGenerator))
                        .toList())
                .build();
    }

    private static int countAllComments(List<BoardComment> comments) {
        int count = 0;
        for (BoardComment comment : comments) {
            count += 1 + countAllComments(comment.getChildComments());
        }
        return count;
    }
}