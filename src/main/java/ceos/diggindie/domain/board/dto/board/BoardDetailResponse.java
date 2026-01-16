package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardComment;
import lombok.Builder;

import java.util.List;

@Builder
public record BoardDetailResponse(
        Long boardId,
        BoardCategory category,
        String title,
        String writerNickname,
        String createdAt,
        String content,
        List<String> imageUrls,
        Integer likeCount,
        Integer commentCount,
        List<CommentResponse> comments
) {
    public static BoardDetailResponse of(Board board, List<BoardComment> comments) {
        int totalCommentCount = comments.stream()
                .mapToInt(c -> 1 + c.getChildComments().size())
                .sum();

        return BoardDetailResponse.builder()
                .boardId(board.getId())
                .category(board.getCategory())
                .title(board.getTitle())
                .writerNickname(board.getIsAnonymous() ? "익명" : board.getMember().getUserId())
                .createdAt(TimeUtils.toRelativeTime(board.getCreatedAt()))
                .content(board.getContent())
                .imageUrls(board.getBoardImages().stream()
                        .map(img -> img.getImageUrl())
                        .toList())
                .likeCount(board.getBoardLikes().size())
                .commentCount(totalCommentCount)
                .comments(comments.stream()
                        .map(CommentResponse::from)
                        .toList())
                .build();
    }
}