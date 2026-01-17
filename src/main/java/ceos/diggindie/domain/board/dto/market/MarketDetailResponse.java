package ceos.diggindie.domain.board.dto.market;

import ceos.diggindie.common.enums.MarketType;
import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.board.entity.market.MarketImage;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public record MarketDetailResponse(
        Long marketId,
        String title,
        String content,
        Integer price,
        String chatUrl,
        MarketType type,
        String nickname,
        String timeAgo,
        Integer views,
        @JsonProperty("isScraped")
        boolean isScraped,
        long scrapCount,
        @JsonProperty("isMine")
        boolean isMine,
        List<MarketImageDTO> images
) {
    public static MarketDetailResponse of(Market market, Long memberId, boolean isScraped, long scrapCount) {
        List<MarketImageDTO> images = market.getMarketImages().stream()
                .sorted(Comparator.comparing(MarketImage::getImageOrder))
                .map(img -> new MarketImageDTO(img.getImageUrl(), img.getImageOrder()))
                .toList();

        boolean isMine = market.getMember().getId().equals(memberId);

        return new MarketDetailResponse(
                market.getId(),
                market.getTitle(),
                market.getContent(),
                market.getPrice(),
                market.getChatUrl(),
                market.getType(),
                market.getMember().getUserId(),
                formatTimeAgo(market.getCreatedAt()),
                market.getViews(),
                isScraped,
                scrapCount,
                isMine,
                images
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

    public record MarketImageDTO(
            String imageUrl,
            Integer imageOrder
    ) {}
}