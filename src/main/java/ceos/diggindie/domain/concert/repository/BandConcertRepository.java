package ceos.diggindie.domain.concert.repository;

import ceos.diggindie.domain.concert.entity.BandConcert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BandConcertRepository extends JpaRepository<BandConcert, Long> {

    /**
     * 선호 밴드가 포함된 공연을 선호 밴드 포함 개수 내림차순으로 조회
     */
    @Query("""
            SELECT bc.concert.id, COUNT(bc.band.id) as bandCount
            FROM BandConcert bc
            WHERE bc.band.id IN :bandIds
              AND bc.concert.startDate >= :now
            GROUP BY bc.concert.id
            ORDER BY bandCount DESC
            """)
    List<Object[]> findConcertIdsByBandIdsOrderByBandCount(
            @Param("bandIds") List<Long> bandIds,
            @Param("now") LocalDateTime now);
}
