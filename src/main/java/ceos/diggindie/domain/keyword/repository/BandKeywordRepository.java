package ceos.diggindie.domain.keyword.repository;

import ceos.diggindie.domain.keyword.entity.BandKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BandKeywordRepository extends JpaRepository<BandKeyword, Long> {

    boolean existsByBandId(Long bandId);

    void deleteAllByBandId(Long bandId);
}