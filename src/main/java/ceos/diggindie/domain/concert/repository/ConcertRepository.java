package ceos.diggindie.domain.concert.repository;

import ceos.diggindie.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @Query(value = """
            SELECT c
            FROM Concert c
            JOIN FETCH c.concertHall
            WHERE c.startDate >= :startOfDay
              AND c.startDate <  :endOfDay
            ORDER BY c.startDate ASC
        """,
        countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE c.startDate >= :startOfDay
              AND c.startDate <  :endOfDay
        """)
    Page<Concert> findByDate(@Param("startOfDay") LocalDateTime startOfDay,
                             @Param("endOfDay") LocalDateTime endOfDay,
                             Pageable pageable);

    List<Concert> findByIdIn(List<Long> ids);


    @Query("""
        SELECT DISTINCT c 
        FROM Concert c
        LEFT JOIN FETCH c.bandConcerts bc
        LEFT JOIN FETCH bc.band
        WHERE c.id IN :concertIds
        """)
    List<Concert> findAllWithBandsByIdIn(@Param("concertIds") List<Long> concertIds);

    @Query("""
        SELECT c
        FROM Concert c
        LEFT JOIN FETCH c.bandConcerts bc
        LEFT JOIN FETCH bc.band
        LEFT JOIN c.concertScraps cs
        WHERE c.startDate > :now
          AND c.id NOT IN :excludeIds
        GROUP BY c
        ORDER BY COUNT(cs) DESC, c.startDate ASC
        """)
    List<Concert> findTopScrappedConcertsAfterNowExcludeIds(
        @Param("now") LocalDateTime now,
        @Param("excludeIds") List<Long> excludeIds,
        Pageable pageable
    );
}
