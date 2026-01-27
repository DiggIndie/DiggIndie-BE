package ceos.diggindie.domain.board.repository;

import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.board.entity.market.MarketScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MarketScrapRepository extends JpaRepository<MarketScrap, Long> {

    Optional<MarketScrap> findByMemberIdAndMarketId(Long memberId, Long marketId);

    boolean existsByMemberIdAndMarketId(Long memberId, Long marketId);

    long countByMarketId(Long marketId);

    @Query("""
    SELECT m FROM MarketScrap ms
    JOIN ms.market m
    WHERE ms.member = :member
""")
    Page<Market> findMarketsByMember(@Param("member") Member member, Pageable pageable);
}