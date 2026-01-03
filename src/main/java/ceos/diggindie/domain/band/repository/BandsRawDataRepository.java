package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.BandsRawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BandsRawDataRepository extends JpaRepository<BandsRawData, Long> {
}
