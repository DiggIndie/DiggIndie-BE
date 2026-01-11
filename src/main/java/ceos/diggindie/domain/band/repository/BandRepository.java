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

    @Query(value = "SELECT DISTINCT b FROM Band b " +
            "LEFT JOIN FETCH b.bandKeywords bk " +
            "LEFT JOIN FETCH bk.keyword k " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "b.bandName ILIKE %:query% OR " + // ILIKE + GIN
            "k.keyword ILIKE %:query%",
            countQuery = "SELECT count(DISTINCT b) FROM Band b " +
                    "LEFT JOIN b.bandKeywords bk " +
                    "LEFT JOIN bk.keyword k " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "b.bandName ILIKE %:query% OR " +
                    "k.keyword ILIKE %:query%")
    Page<Band> searchBands(@Param("query") String query, Pageable pageable);

    Optional<Band> findById(Long bandId);

    @Query("SELECT b FROM Band b " +
            "LEFT JOIN FETCH b.bandKeywords bk " +
            "LEFT JOIN FETCH bk.keyword " +
            "WHERE b.id = :bandId")
    Optional<Band> findByIdWithKeywords(@Param("bandId") Long bandId);

    @Query("SELECT b FROM Band b " +
            "LEFT JOIN FETCH b.artists " +
            "WHERE b.id = :bandId")
    Optional<Band> findByIdWithArtists(@Param("bandId") Long bandId);
}
