package ceos.diggindie.domain.member.dto.mypage;

import ceos.diggindie.common.utils.TimeUtils;
import ceos.diggindie.domain.board.entity.market.Market;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.yaml.snakeyaml.error.Mark;

@Builder
public record MyMarketPostResponse(
        Long marketId,
        String category,
        String title,
        Integer price,
        String thumbnailUrl,
        int scrapCount,
        String createdAt
) {
    public static MyMarketPostResponse from(Market market) {
        return MyMarketPostResponse.builder()
                .marketId(market.getId())
                .category(market.getType() != null ? market.getType().getDisplayName() : null)
                .title(market.getTitle())
                .price(market.getPrice())
                .thumbnailUrl(market.getMarketImages() != null && !market.getMarketImages().isEmpty()
                        ? market.getMarketImages().get(0).getImageUrl()
                        : null)
                .scrapCount(market.getMarketScraps().size())
                .createdAt(TimeUtils.toRelativeTime(market.getCreatedAt()))
                .build();
    }
}