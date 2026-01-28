package ceos.diggindie.domain.board.dto;

import ceos.diggindie.common.enums.PostType;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.market.Market;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotPostResponse {
    private Long id;
    private String category;     // "MARKET", "BOARD"
    private String subCategory;  // Market의 type, Board의 category
    private String title;
    private Integer views;

    public static HotPostResponse fromMarket(Market market) {
        return HotPostResponse.builder()
                .id(market.getId())
                .category(PostType.MARKET.getValue())
                .subCategory(market.getType().name())
                .title(market.getTitle())
                .views(market.getViews())
                .build();
    }

    public static HotPostResponse fromBoard(Board board) {
        return HotPostResponse.builder()
                .id(board.getId())
                .category(PostType.BOARD.getValue())
                .subCategory(board.getCategory().name())
                .title(board.getTitle())
                .views(board.getViews())
                .build();
    }
}