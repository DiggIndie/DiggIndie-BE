package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.domain.board.entity.board.Board;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotBoardResponse {
    private Long id;
    private String category;
    private String title;
    private Integer views;

    public static HotBoardResponse fromBoard(Board board) {
        return HotBoardResponse.builder()
                .id(board.getId())
                .category(board.getCategory().getDescription())
                .title(board.getTitle())
                .views(board.getViews())
                .build();
    }
}