package ceos.diggindie.domain.board.repository;

import ceos.diggindie.common.enums.MarketType;
import ceos.diggindie.domain.board.entity.board.Board;
import ceos.diggindie.domain.board.entity.market.Market;
import ceos.diggindie.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {

    @Query("SELECT DISTINCT m FROM Market m " +
            "LEFT JOIN FETCH m.marketImages " +
            "WHERE m.id = :marketId")
    Optional<Market> findByIdWithImages(@Param("marketId") Long marketId);

    @Query(value = "SELECT DISTINCT m FROM Market m " +
            "LEFT JOIN FETCH m.marketImages " +
            "ORDER BY m.createdAt DESC",
            countQuery = "SELECT COUNT(m) FROM Market m")
    Page<Market> findAllWithImages(Pageable pageable);

    @Query(value = "SELECT DISTINCT m FROM Market m " +
            "LEFT JOIN FETCH m.marketImages " +
            "WHERE m.type = :type " +
            "ORDER BY m.createdAt DESC",
            countQuery = "SELECT COUNT(m) FROM Market m WHERE m.type = :type")
    Page<Market> findByTypeWithImages(@Param("type") MarketType type, Pageable pageable);

    @Query(value = "SELECT DISTINCT m FROM Market m " +
            "LEFT JOIN FETCH m.marketImages " +
            "WHERE m.title ILIKE %:query% OR m.content ILIKE %:query% " +
            "ORDER BY m.createdAt DESC",
            countQuery = "SELECT COUNT(m) FROM Market m WHERE m.title ILIKE %:query% OR m.content ILIKE %:query%")
    Page<Market> findAllByQueryWithImages(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT DISTINCT m FROM Market m " +
            "LEFT JOIN FETCH m.marketImages " +
            "WHERE m.type = :type AND (m.title ILIKE %:query% OR m.content ILIKE %:query%) " +
            "ORDER BY m.createdAt DESC",
            countQuery = "SELECT COUNT(m) FROM Market m WHERE m.type = :type AND (m.title ILIKE %:query% OR m.content ILIKE %:query%)")
    Page<Market> findByTypeAndQueryWithImages(@Param("type") MarketType type, @Param("query") String query, Pageable pageable);

    Page<Market> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

}