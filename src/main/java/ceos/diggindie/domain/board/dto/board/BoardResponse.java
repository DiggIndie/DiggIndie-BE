package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.domain.board.entity.board.Board;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BoardResponse(
        Long boardId,
        String title,
        String content,
        Boolean isAnonymous,
        String writerNickname,
        BoardCategory category,
        Integer views,
        Integer likeCount,
        List<String> imageUrls,
        LocalDateTime createdAt
) {
    public static BoardResponse from(Board board) {
        return BoardResponse.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .isAnonymous(board.getIsAnonymous())
                .writerNickname(board.getIsAnonymous() ? "익명" : board.getMember().getUserId())
                .category(board.getCategory())
                .views(board.getViews())
                .likeCount(board.getBoardLikes().size())
                .imageUrls(board.getBoardImages().stream()
                        .map(img -> img.getImageUrl())
                        .toList())
                .createdAt(board.getCreatedAt())
                .build();
    }
}