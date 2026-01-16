package ceos.diggindie.domain.board.dto.board;

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
        Integer commentCount,
        List<CommentResponse> comments
) {
    public static BoardDetailResponse of(Board board, List<BoardComment> comments) {
        int totalCommentCount = countAllComments(comments);

        return BoardDetailResponse.builder()
                .boardId(board.getId())
                .category(board.getCategory().getDescription())
                .title(board.getTitle())
                .writerNickname(board.getMember().getUserId())
                .createdAt(TimeUtils.toRelativeTime(board.getCreatedAt()))
                .content(board.getContent())
                .imageUrls(board.getBoardImages().stream()
                        .map(img -> img.getImageUrl())
                        .toList())
                .views(board.getViews())
                .likeCount(board.getBoardLikes().size())
                .commentCount(totalCommentCount)
                .comments(comments.stream()
                        .map(CommentResponse::from)
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