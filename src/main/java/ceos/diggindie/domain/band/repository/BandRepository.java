package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Band;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BandRepository extends JpaRepository<Band, Long> {
    Optional<Band> findByBandName(String bandName);

    @Query("SELECT DISTINCT b FROM Band b " +
            "LEFT JOIN b.bandKeywords bk " +
            "LEFT JOIN bk.keyword k " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "b.bandName LIKE %:query% OR k.keyword LIKE %:query%")
    Page<Band> searchBands(@Param("query") String query, Pageable pageable);
}
