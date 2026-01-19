package ceos.diggindie.domain.board.dto.board;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.board.Board;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

public record BoardListResponse(
        List<Item> boards,
        PageInfo pageInfo
) {
    public static BoardListResponse from(Page<Board> boardPage) {
        List<Item> boards = boardPage.getContent().stream()
                .map(Item::from)
                .toList();

        PageInfo pageInfo = new PageInfo(
                boardPage.getNumber(),
                boardPage.getSize(),
                boardPage.hasNext(),
                boardPage.getTotalElements(),
                boardPage.getTotalPages()
        );

        return new BoardListResponse(boards, pageInfo);
    }

    @Builder
    public record Item(
            Long boardId,
            String category,
            String title,
            String createdAt,
            Integer views,
            Integer imageCount
    ) {
        public static Item from(Board board) {
            return Item.builder()
                    .boardId(board.getId())
                    .category(board.getCategory().getDescription())
                    .title(board.getTitle())
                    .createdAt(TimeUtils.toRelativeTime(board.getCreatedAt()))
                    .views(board.getViews())
                    .imageCount(board.getBoardImages().size())
                    .build();
        }
    }
}