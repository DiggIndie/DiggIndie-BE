package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Band;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BandRepository extends JpaRepository<Band, Long> {
}
