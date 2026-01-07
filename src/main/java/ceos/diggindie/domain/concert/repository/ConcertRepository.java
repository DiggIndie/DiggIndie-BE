package ceos.diggindie.domain.concert.repository;

import ceos.diggindie.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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

    // 공연 임박순 (오늘 이후 공연만, startDate 오름차순)
    @Query(value = """
            SELECT c
            FROM Concert c
            LEFT JOIN FETCH c.concertHall
            LEFT JOIN FETCH c.bandConcerts bc
            LEFT JOIN FETCH bc.band
            WHERE c.startDate >= :now
              AND (:query IS NULL OR c.title LIKE CONCAT('%', :query, '%'))
            ORDER BY c.startDate ASC
        """,
            countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE c.startDate >= :now
                AND (:query IS NULL OR c.title LIKE CONCAT('%', :query, '%'))
        """)
    Page<Concert> findAllByRecent(@Param("query") String query,
                                   @Param("now") LocalDateTime now,
                                   Pageable pageable);

    // 조회순 (views 내림차순)
    @Query(value = """
            SELECT c
            FROM Concert c
            LEFT JOIN FETCH c.concertHall
            LEFT JOIN FETCH c.bandConcerts bc
            LEFT JOIN FETCH bc.band
            WHERE (:query IS NULL OR c.title LIKE CONCAT('%', :query, '%'))
            ORDER BY c.views DESC, c.startDate ASC
        """,
            countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE (:query IS NULL OR c.title LIKE CONCAT('%', :query, '%'))
        """)
    Page<Concert> findAllByViews(@Param("query") String query,
                                  Pageable pageable);

    // 스크랩순 (스크랩 수 내림차순)
    @Query(value = """
            SELECT c
            FROM Concert c
            LEFT JOIN FETCH c.concertHall
            LEFT JOIN FETCH c.bandConcerts bc
            LEFT JOIN FETCH bc.band
            LEFT JOIN c.concertScraps cs
            WHERE (:query IS NULL OR c.title LIKE CONCAT('%', :query, '%'))
            GROUP BY c.id
            ORDER BY COUNT(cs) DESC, c.startDate ASC
        """,
            countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE (:query IS NULL OR c.title LIKE CONCAT('%', :query, '%'))
        """)
    Page<Concert> findAllByScrapCount(@Param("query") String query,
                                       Pageable pageable);

}
