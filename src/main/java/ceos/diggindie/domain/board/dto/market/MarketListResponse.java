package ceos.diggindie.domain.board.dto.market;

import ceos.diggindie.common.enums.MarketType;
import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.board.entity.market.MarketImage;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public record MarketListResponse(
        List<MarketSummary> markets,
        int currentPage,
        int totalPages,
        long totalElements,
        boolean hasNext
) {
    public static MarketListResponse from(Page<Market> page) {
        List<MarketSummary> markets = page.getContent().stream()
                .map(MarketSummary::from)
                .toList();

        return new MarketListResponse(
                markets,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext()
        );
    }

    public record MarketSummary(
            Long marketId,
            String title,
            Integer price,
            MarketType type,
            String nickname,
            String timeAgo,
            Integer views,
            long scrapCount,
            String thumbnailUrl
    ) {
        public static MarketSummary from(Market market) {
            String thumbnail = market.getMarketImages().stream()
                    .min(Comparator.comparing(MarketImage::getImageOrder))
                    .map(MarketImage::getImageUrl)
                    .orElse(null);

            return new MarketSummary(
                    market.getId(),
                    market.getTitle(),
                    market.getPrice(),
                    market.getType(),
                    market.getMember().getUserId(),
                    formatTimeAgo(market.getCreatedAt()),
                    market.getViews(),
                    market.getMarketScraps().size(),
                    thumbnail
            );
        }

        private static String formatTimeAgo(LocalDateTime createdAt) {
            LocalDateTime now = LocalDateTime.now();
            long minutes = ChronoUnit.MINUTES.between(createdAt, now);
            long hours = ChronoUnit.HOURS.between(createdAt, now);
            long days = ChronoUnit.DAYS.between(createdAt, now);

            if (minutes < 1) return "방금 전";
            if (minutes < 60) return minutes + "분 전";
            if (hours < 24) return hours + "시간 전";
            if (days < 7) return days + "일 전";
            return createdAt.toLocalDate().toString();
        }
    }
}