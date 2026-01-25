package ceos.diggindie.domain.magazine.repository;

import ceos.diggindie.domain.magazine.entity.Magazine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    @Query(value = "SELECT DISTINCT m FROM Magazine m " +
            "LEFT JOIN FETCH m.magazineImages " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "m.title ILIKE %:query% " +
            "ORDER BY m.createdAt DESC",
            countQuery = "SELECT count(DISTINCT m) FROM Magazine m " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "m.title ILIKE %:query%")
    Page<Magazine> searchMagazinesByRecent(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT DISTINCT m FROM Magazine m " +
            "LEFT JOIN FETCH m.magazineImages " +
            "WHERE :query IS NULL OR :query = '' OR " +
            "m.title ILIKE %:query% " +
            "ORDER BY m.view DESC, m.createdAt DESC",
            countQuery = "SELECT count(DISTINCT m) FROM Magazine m " +
                    "WHERE :query IS NULL OR :query = '' OR " +
                    "m.title ILIKE %:query%")
    Page<Magazine> searchMagazinesByView(@Param("query") String query, Pageable pageable);
}

