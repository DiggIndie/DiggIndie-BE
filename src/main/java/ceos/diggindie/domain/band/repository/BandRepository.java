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
            "b.bandName ILIKE %:query% OR " +
            "k.keyword ILIKE %:query%",
            countQuery = "SELECT count(DISTINCT b) FROM Band b " +
                    "LEFT JOIN b.bandKeywords bk " +
                    "LEFT JOIN bk.keyword k " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "b.bandName ILIKE %:query% OR " +
                    "k.keyword ILIKE %:query%")
    Page<Band> searchBands(@Param("query") String query, Pageable pageable);

    // 최신순 정렬
    @Query(value = "SELECT DISTINCT b FROM Band b " +
            "LEFT JOIN FETCH b.bandKeywords bk " +
            "LEFT JOIN FETCH bk.keyword k " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "b.bandName ILIKE %:query% OR " +
            "k.keyword ILIKE %:query% " +
            "ORDER BY b.createdAt DESC",
            countQuery = "SELECT count(DISTINCT b) FROM Band b " +
                    "LEFT JOIN b.bandKeywords bk " +
                    "LEFT JOIN bk.keyword k " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "b.bandName ILIKE %:query% OR " +
                    "k.keyword ILIKE %:query%")
    Page<Band> searchBandsByRecent(@Param("query") String query, Pageable pageable);

    // 가나다순 정렬
    @Query(value = "SELECT * FROM (" +
            "SELECT DISTINCT b.* FROM band b " +
            "LEFT JOIN band_keyword bk ON b.band_id = bk.band_id " +
            "LEFT JOIN keyword k ON bk.keyword_id = k.keyword_id " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "b.band_name ILIKE CONCAT('%', :query, '%') OR " +
            "k.keyword ILIKE CONCAT('%', :query, '%')" +
            ") AS distinct_bands " +
            "ORDER BY band_name COLLATE \"ko_KR.utf8\" ASC",
            countQuery = "SELECT count(DISTINCT b.band_id) FROM band b " +
                    "LEFT JOIN band_keyword bk ON b.band_id = bk.band_id " +
                    "LEFT JOIN keyword k ON bk.keyword_id = k.keyword_id " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "b.band_name ILIKE CONCAT('%', :query, '%') OR " +
                    "k.keyword ILIKE CONCAT('%', :query, '%')",
            nativeQuery = true)
    Page<Band> searchBandsByAlphabet(@Param("query") String query, Pageable pageable);

    // 스크랩순 정렬
    @Query(value = "SELECT DISTINCT b FROM Band b " +
            "LEFT JOIN FETCH b.bandKeywords bk " +
            "LEFT JOIN FETCH bk.keyword k " +
            "LEFT JOIN b.bandScraps bs " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "b.bandName ILIKE %:query% OR " +
            "k.keyword ILIKE %:query% " +
            "GROUP BY b.id " +
            "ORDER BY COUNT(bs) DESC, b.bandName ASC",
            countQuery = "SELECT count(DISTINCT b) FROM Band b " +
                    "LEFT JOIN b.bandKeywords bk " +
                    "LEFT JOIN bk.keyword k " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "b.bandName ILIKE %:query% OR " +
                    "k.keyword ILIKE %:query%")
    Page<Band> searchBandsByScrap(@Param("query") String query, Pageable pageable);
}
