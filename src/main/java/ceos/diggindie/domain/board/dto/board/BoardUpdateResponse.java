package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.enums.BoardCategory;
import ceos.diggindie.domain.board.entity.board.Board;

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
        return new BoardUpdateResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getIsAnonymous(),
                board.getCategory(),
                board.getImageUrls(),
                board.getUpdatedAt()
        );
    }
}