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

}
