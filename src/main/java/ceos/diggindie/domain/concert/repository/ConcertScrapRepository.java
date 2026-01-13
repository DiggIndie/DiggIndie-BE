package ceos.diggindie.domain.concert.repository;

import ceos.diggindie.domain.concert.entity.ConcertScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScrapRepository extends JpaRepository<ConcertScrap, Long> {

    /**
     * 스크랩이 많은 공연 ID 목록을 조회
     * 특정 공연 ID는 제외
     */
    @Query("""
            SELECT cs.concert.id, COUNT(cs.id) as scrapCount
            FROM ConcertScrap cs
            WHERE cs.concert.startDate >= :now
              AND cs.concert.id NOT IN :excludeIds
            GROUP BY cs.concert.id
            ORDER BY scrapCount DESC
            """)
    List<Object[]> findMostScrappedConcertIds(
            @Param("now") LocalDateTime now,
            @Param("excludeIds") List<Long> excludeIds);

    /**
     * 스크랩이 많은 공연 ID 목록을 조회 (시작일이 현재 이후인 공연만)
     * 제외할 공연이 없는 경우
     */
    @Query("""
            SELECT cs.concert.id, COUNT(cs.id) as scrapCount
            FROM ConcertScrap cs
            WHERE cs.concert.startDate >= :now
            GROUP BY cs.concert.id
            ORDER BY scrapCount DESC
            """)
    List<Object[]> findMostScrappedConcertIds(@Param("now") LocalDateTime now);

    /**
     * 특정 멤버의 스크랩 목록을 조회합니다.
     * fetch join을 사용하여 연관된 Concert 엔티티를 한 번에 가져옵니다. (N+1 방지)
     */
    @Query("SELECT cs FROM ConcertScrap cs " +
            "JOIN FETCH cs.concert " +
            "WHERE cs.member.id = :memberId")
    List<ConcertScrap> findAllByMemberIdWithConcert(@Param("memberId") Long memberId);

    /**
     * 특정 멤버가 특정 공연을 이미 스크랩했는지 확인 (중복 체크용 - 필요시 사용)
     */
    boolean existsByMemberIdAndConcertId(Long memberId, Long concertId);

    /**
     * 스크랩 취소 시 사용 (삭제)
     */
    void deleteByMemberIdAndConcertId(Long memberId, Long concertId);
}