package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Band;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BandRepository extends JpaRepository<Band, Long> {
    
    Optional<Band> findByBandName(String bandName);

    // 온보딩용 기본 밴드 검색
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

    // [최신순] 아티스트 검색
    @Query(value = "SELECT DISTINCT b FROM Band b " +
            "LEFT JOIN FETCH b.topTrack " +
            "LEFT JOIN b.bandKeywords bk " +
            "LEFT JOIN bk.keyword k " +
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

    // [가나다순] 아티스트 검색 (Native Query)
    @Query(value = "SELECT b.band_id FROM band b " +
            "WHERE b.band_id IN (" +
            "  SELECT DISTINCT b2.band_id FROM band b2 " +
            "  LEFT JOIN band_keyword bk ON b2.band_id = bk.band_id " +
            "  LEFT JOIN keyword k ON k.keyword_id = bk.keyword_id " +
            "  WHERE :query IS NULL OR :query = '' OR " +
            "  b2.band_name ILIKE CONCAT('%', :query, '%') OR " +
            "  k.keyword ILIKE CONCAT('%', :query, '%')" +
            ") " +
            "ORDER BY LOWER(b.band_name) COLLATE \"ko_KR.utf8\" ASC",
            countQuery = "SELECT count(DISTINCT b.band_id) FROM band b " +
                    "LEFT JOIN band_keyword bk ON b.band_id = bk.band_id " +
                    "LEFT JOIN keyword k ON k.keyword_id = bk.keyword_id " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "b.band_name ILIKE CONCAT('%', :query, '%') OR " +
                    "k.keyword ILIKE CONCAT('%', :query, '%')",
            nativeQuery = true)
    Page<Long> searchBandIdsByAlphabet(@Param("query") String query, Pageable pageable);

    // ID 목록으로 Band + TopTrack 함께 조회 (N+1 방지)
    @Query("SELECT DISTINCT b FROM Band b LEFT JOIN FETCH b.topTrack WHERE b.id IN :ids")
    List<Band> findByIdInWithTopTrack(@Param("ids") List<Long> ids);

    // [스크랩순] 아티스트 검색
    @Query(value = "SELECT b FROM Band b " +
            "LEFT JOIN FETCH b.topTrack " +
            "LEFT JOIN b.bandKeywords bk " +
            "LEFT JOIN bk.keyword k " +
            "LEFT JOIN b.bandScraps bs " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "b.bandName ILIKE %:query% OR " +
            "k.keyword ILIKE %:query% " +
            "GROUP BY b.id, b.topTrack.id " +
            "ORDER BY COUNT(DISTINCT bs.id) DESC, b.createdAt DESC",
            countQuery = "SELECT count(DISTINCT b) FROM Band b " +
                    "LEFT JOIN b.bandKeywords bk " +
                    "LEFT JOIN bk.keyword k " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "b.bandName ILIKE %:query% OR " +
                    "k.keyword ILIKE %:query%")
    Page<Band> searchBandsByScrap(@Param("query") String query, Pageable pageable);

    // 아티스트 상세 조회 (연관된 모든 정보 Fetch Join)
    @Query("SELECT DISTINCT b FROM Band b " +
            "LEFT JOIN FETCH b.bandKeywords bk " +
            "LEFT JOIN FETCH bk.keyword " +
            "LEFT JOIN FETCH b.artists " +
            "LEFT JOIN FETCH b.topTrack " +
            "WHERE b.id = :bandId")
    Optional<Band> findByIdWithDetails(@Param("bandId") Long bandId);
}