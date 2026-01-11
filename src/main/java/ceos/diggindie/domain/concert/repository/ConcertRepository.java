package ceos.diggindie.domain.concert.repository;

import ceos.diggindie.domain.concert.entity.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @Query("""
            SELECT c
            FROM Concert c
            JOIN FETCH c.concertHall
            LEFT JOIN FETCH c.bandConcerts bc
            LEFT JOIN FETCH bc.band
            WHERE c.id = :concertId
        """)
    Optional<Concert> findByIdWithDetails(@Param("concertId") Long concertId);

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

    // 최근(빠른 시작일) 순 정렬
    @Query(value = """
            SELECT c
            FROM Concert c
            JOIN FETCH c.concertHall
            WHERE c.startDate >= :now
              AND (:query IS NULL OR :query = '' OR c.title LIKE CONCAT('%', :query, '%'))
            ORDER BY c.startDate ASC
        """,
            countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE c.startDate >= :now
              AND (:query IS NULL OR :query = '' OR c.title LIKE CONCAT('%', :query, '%'))
        """)
    Page<Concert> findAllByRecent(@Param("query") String query,
                                  @Param("now") LocalDateTime now,
                                  Pageable pageable);

    // 조회수 순 정렬
    @Query(value = """
            SELECT c
            FROM Concert c
            JOIN FETCH c.concertHall
            WHERE c.startDate >= :now
              AND (:query IS NULL OR :query = '' OR c.title LIKE CONCAT('%', :query, '%'))
            ORDER BY c.views DESC, c.startDate ASC
        """,
            countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE c.startDate >= :now
              AND (:query IS NULL OR :query = '' OR c.title LIKE CONCAT('%', :query, '%'))
        """)
    Page<Concert> findAllByViews(@Param("query") String query,
                                 @Param("now") LocalDateTime now,
                                 Pageable pageable);

    // 스크랩 수 순 정렬
    @Query(value = """
            SELECT c
            FROM Concert c
            JOIN FETCH c.concertHall
            LEFT JOIN c.concertScraps cs
            WHERE c.startDate >= :now
              AND (:query IS NULL OR :query = '' OR c.title LIKE CONCAT('%', :query, '%'))
            GROUP BY c.id
            ORDER BY COUNT(cs) DESC, c.startDate ASC
        """,
            countQuery = """
            SELECT COUNT(DISTINCT c)
            FROM Concert c
            LEFT JOIN c.concertScraps cs
            WHERE c.startDate >= :now
              AND (:query IS NULL OR :query = '' OR c.title LIKE CONCAT('%', :query, '%'))
        """)
    Page<Concert> findAllByScrapCount(@Param("query") String query,
                                      @Param("now") LocalDateTime now,
                                      Pageable pageable);

    // 특정 월에 공연이 있는 날짜 목록 조회
    @Query("""
            SELECT DISTINCT CAST(c.startDate AS LocalDate)
            FROM Concert c
            WHERE c.startDate >= :startOfMonth
              AND c.startDate < :endOfMonth
        """)
    List<LocalDate> findDistinctConcertDatesByMonth(
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth);

}
