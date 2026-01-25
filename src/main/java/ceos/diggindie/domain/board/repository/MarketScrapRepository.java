package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.market.MarketScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketScrapRepository extends JpaRepository<MarketScrap, Long> {

    Optional<MarketScrap> findByMemberIdAndMarketId(Long memberId, Long marketId);

    boolean existsByMemberIdAndMarketId(Long memberId, Long marketId);

    long countByMarketId(Long marketId);
}