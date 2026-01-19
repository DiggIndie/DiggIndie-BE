package ceos.diggindie.domain.board.dto.market;

public record ScrapResponse(
        boolean isScraped,
        long scrapCount
) {
    public static ScrapResponse of(boolean isScraped, long scrapCount) {
        return new ScrapResponse(isScraped, scrapCount);
    }
}