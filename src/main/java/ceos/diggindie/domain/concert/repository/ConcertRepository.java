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


    /**
     * 공연 검색 - 공연임박순 (recent) - ID만 조회
     * 정렬: 진행 예정 공연(startDate >= now)을 빠른 순으로 먼저, 종료된 공연은 최근 순으로 뒤에
     * 검색: 공연 제목 또는 밴드 이름에 query 포함
     */
    @Query(value = """
        SELECT c.id
        FROM Concert c
        WHERE (:query IS NULL OR :query = ''
            OR c.title LIKE CONCAT('%', :query, '%')
            OR EXISTS (
                SELECT 1
                FROM BandConcert bc
                JOIN bc.band b
                WHERE bc.concert = c
                    AND b.bandName LIKE CONCAT('%', :query, '%')
            ))
        ORDER BY
            CASE WHEN c.startDate >= :now THEN 0 ELSE 1 END,
            CASE WHEN c.startDate >= :now THEN c.startDate END ASC,
            CASE WHEN c.startDate <  :now THEN c.startDate END DESC
    """,
    countQuery = """
        SELECT COUNT(c)
        FROM Concert c
        WHERE (:query IS NULL OR :query = ''
            OR c.title LIKE CONCAT('%', :query, '%')
            OR EXISTS (
                SELECT 1
                FROM BandConcert bc
                JOIN bc.band b
                WHERE bc.concert = c
                    AND b.bandName LIKE CONCAT('%', :query, '%')
            ))
    """)
    Page<Long> findConcertIdsByRecent(@Param("query") String query,
                                    @Param("now") LocalDateTime now,
                                    Pageable pageable);

    /**
     * 공연 검색 - 조회수순 (view) - ID만 조회
     * 정렬: 조회수 높은 순
     * 검색: 공연 제목 또는 밴드 이름에 query 포함
     */
    @Query(value = """
            SELECT c.id
            FROM Concert c
            WHERE (:query IS NULL OR :query = ''
                OR c.title LIKE CONCAT('%', :query, '%')
                OR EXISTS (
                    SELECT 1
                    FROM BandConcert bc
                    JOIN bc.band b
                    WHERE bc.concert = c
                        AND b.bandName LIKE CONCAT('%', :query, '%')
                ))
            ORDER BY c.views DESC, c.id DESC
            """,
            countQuery = """
            SELECT COUNT(c)
            FROM Concert c
            WHERE (:query IS NULL OR :query = ''
                OR c.title LIKE CONCAT('%', :query, '%')
                OR EXISTS (
                    SELECT 1
                    FROM BandConcert bc
                    JOIN bc.band b
                    WHERE bc.concert = c
                        AND b.bandName LIKE CONCAT('%', :query, '%')
                ))
            """)
    Page<Long> findConcertIdsByViews(@Param("query") String query,
                                    @Param("now") LocalDateTime now,
                                    Pageable pageable);

    /**
     * 공연 검색 - 스크랩순 (scrap) - ID만 조회
     * 정렬: 스크랩 개수 많은 순
     * 검색: 공연 제목 또는 밴드 이름에 query 포함
     */
    @Query(value = """
            SELECT c.id
            FROM Concert c
            LEFT JOIN c.concertScraps cs
            WHERE (:query IS NULL OR :query = ''
                OR c.title LIKE CONCAT('%', :query, '%')
                OR EXISTS (
                    SELECT 1
                    FROM BandConcert bc
                    JOIN bc.band b
                    WHERE bc.concert = c
                        AND b.bandName LIKE CONCAT('%', :query, '%')
                ))
            GROUP BY c.id
            ORDER BY COUNT(cs) DESC, c.id DESC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT c)
            FROM Concert c
            WHERE (:query IS NULL OR :query = ''
                OR c.title LIKE CONCAT('%', :query, '%')
                OR EXISTS (
                    SELECT 1
                    FROM BandConcert bc
                    JOIN bc.band b
                    WHERE bc.concert = c
                        AND b.bandName LIKE CONCAT('%', :query, '%')
                ))
            """)
    Page<Long> findConcertIdsByScrapCount(@Param("query") String query,
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

    boolean existsByTitleAndStartDate(String title, LocalDateTime startDate);
}