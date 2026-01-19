package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.board.BoardImage;

import java.time.LocalDateTime;
import java.util.List;

public record BoardUpdateResponse(
        Long boardId,
        String title,
        String content,
        Boolean isAnonymous,
        BoardCategory category,
        List<String> imageUrls,
        LocalDateTime updatedAt
) {
    public static BoardUpdateResponse from(Board board) {
        List<String> imageUrls = board.getBoardImages().stream()
                .map(BoardImage::getImageUrl)
                .toList();

        return new BoardUpdateResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getIsAnonymous(),
                board.getCategory(),
                imageUrls,
                board.getUpdatedAt()
        );
    }
}