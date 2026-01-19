package ceos.diggindie.domain.board.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.enums.MarketType;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.board.dto.market.*;
import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.board.entity.market.MarketImage;
import ceos.diggindie.domain.board.entity.market.MarketScrap;
import ceos.diggindie.domain.board.repository.MarketRepository;
import ceos.diggindie.domain.board.repository.MarketScrapRepository;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketService {

    private final MarketRepository marketRepository;
    private final MarketScrapRepository marketScrapRepository;
    private final MemberService memberService;

    @Transactional
    public MarketCreateResponse createMarket(Long memberId, MarketCreateRequest request) {
        Member member = memberService.findById(memberId);

        Market market = Market.builder()
                .title(request.title())
                .content(request.content())
                .price(request.price())
                .chatUrl(request.chatUrl())
                .member(member)
                .type(request.type())
                .build();

        if (request.imageUrls() != null) {
            for (int i = 0; i < request.imageUrls().size(); i++) {
                MarketImage image = MarketImage.builder()
                        .imageUrl(request.imageUrls().get(i))
                        .imageOrder(i)
                        .market(market)
                        .build();
                market.addImage(image);
            }
        }

        Market savedMarket = marketRepository.save(market);
        return MarketCreateResponse.from(savedMarket.getId());
    }

    @Transactional
    public MarketDetailResponse getMarketDetail(Long marketId, Long memberId) {
        Market market = marketRepository.findByIdWithImages(marketId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MARKET_NOT_FOUND,
                        "마켓 게시글을 찾을 수 없습니다."));

        market.increaseViews();

        boolean isScraped = marketScrapRepository.existsByMemberIdAndMarketId(memberId, marketId);
        long scrapCount = marketScrapRepository.countByMarketId(marketId);

        return MarketDetailResponse.of(market, memberId, isScraped, scrapCount);
    }

    @Transactional
    public void updateMarket(Long memberId, Long marketId, MarketUpdateRequest request) {
        Market market = marketRepository.findByIdWithImages(marketId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MARKET_NOT_FOUND,
                        "마켓 게시글을 찾을 수 없습니다."));

        // 본인 확인
        if (!market.getMember().getId().equals(memberId)) {
            throw new BusinessException(BusinessErrorCode.MARKET_NOT_OWNER,
                    "본인의 게시글만 수정할 수 있습니다.");
        }

        market.update(request.title(), request.content(), request.price(), request.chatUrl(), request.type());

        // 이미지 교체
        market.clearImages();
        if (request.imageUrls() != null) {
            for (int i = 0; i < request.imageUrls().size(); i++) {
                MarketImage image = MarketImage.builder()
                        .imageUrl(request.imageUrls().get(i))
                        .imageOrder(i)
                        .market(market)
                        .build();
                market.addImage(image);
            }
        }
    }

    @Transactional
    public void deleteMarket(Long memberId, Long marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MARKET_NOT_FOUND,
                        "마켓 게시글을 찾을 수 없습니다."));

        // 본인 확인
        if (!market.getMember().getId().equals(memberId)) {
            throw new BusinessException(BusinessErrorCode.MARKET_NOT_OWNER,
                    "본인의 게시글만 삭제할 수 있습니다.");
        }

        marketRepository.delete(market);
    }

    public MarketListResponse getMarketList(MarketType type, String query, Pageable pageable) {
        Page<Market> markets;
        boolean hasQuery = query != null && !query.isBlank();

        if (type == null) {
            markets = hasQuery
                    ? marketRepository.findAllByQueryWithImages(query, pageable)
                    : marketRepository.findAllWithImages(pageable);
        } else {
            markets = hasQuery
                    ? marketRepository.findByTypeAndQueryWithImages(type, query, pageable)
                    : marketRepository.findByTypeWithImages(type, pageable);
        }

        return MarketListResponse.from(markets);
    }

    @Transactional
    public ScrapResponse toggleMarketScrap(Long memberId, Long marketId) {
        Member member = memberService.findById(memberId);

        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MARKET_NOT_FOUND,
                        "마켓 게시글을 찾을 수 없습니다."));

        // 본인 게시글 스크랩 방지
        if (market.getMember().getId().equals(memberId)) {
            throw new BusinessException(BusinessErrorCode.SELF_SCRAP_NOT_ALLOWED,
                    "자신의 게시글은 스크랩할 수 없습니다.");
        }

        Optional<MarketScrap> existingScrap = marketScrapRepository.findByMemberIdAndMarketId(memberId, marketId);

        boolean isScraped;
        if (existingScrap.isPresent()) {
            marketScrapRepository.delete(existingScrap.get());
            isScraped = false;
        } else {
            marketScrapRepository.save(MarketScrap.builder()
                    .member(member)
                    .market(market)
                    .build());
            isScraped = true;
        }

        long scrapCount = marketScrapRepository.countByMarketId(marketId);
        return ScrapResponse.of(isScraped, scrapCount);
    }
}