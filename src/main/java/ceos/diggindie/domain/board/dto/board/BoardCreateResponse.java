package ceos.diggindie.domain.board.dto.board;

public record BoardCreateResponse(
        Long boardId
) {
    public static BoardCreateResponse from(Long boardId) {
        return new BoardCreateResponse(boardId);
    }
}