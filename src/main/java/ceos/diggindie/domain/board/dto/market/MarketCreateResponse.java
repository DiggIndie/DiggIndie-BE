package ceos.diggindie.domain.board.dto.market;

public record MarketCreateResponse(
        Long marketId
) {
    public static MarketCreateResponse from(Long marketId) {
        return new MarketCreateResponse(marketId);
    }
}