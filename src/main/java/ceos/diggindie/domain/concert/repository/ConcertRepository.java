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

    @Query("SELECT DISTINCT c FROM Concert c " +
            "JOIN FETCH c.bandConcerts cb " +
            "JOIN FETCH cb.band " +
            "WHERE cb.band.id = :bandId " +
            "ORDER BY c.startDate DESC")
    List<Concert> findConcertsByBandId(@Param("bandId") Long bandId);

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

    @Query("""
            SELECT DISTINCT CAST(c.startDate AS LocalDate)
            FROM Concert c
            WHERE c.startDate >= :startOfMonth
              AND c.startDate < :endOfMonth
        """)
    List<LocalDate> findDistinctConcertDatesByMonth(
            @Param("startOfMonth") LocalDateTime startOfMonth,
            @Param("endOfMonth") LocalDateTime endOfMonth);

    @Query(value = """
            SELECT c
            FROM Concert c
            JOIN FETCH c.concertHall
            WHERE CAST(c.startDate AS LocalDate) IN :dates
            ORDER BY c.startDate ASC
        """,
            countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE CAST(c.startDate AS LocalDate) IN :dates
        """)
    Page<Concert> findByDates(@Param("dates") List<LocalDate> dates, Pageable pageable);

    @Query("""
            SELECT c FROM Concert c
            LEFT JOIN FETCH c.bandConcerts bc
            LEFT JOIN FETCH bc.band
            WHERE c.id IN :ids
            """)
    List<Concert> findAllByIdWithBandConcerts(@Param("ids") List<Long> ids);
}