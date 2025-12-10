package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Band;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BandRepository extends JpaRepository<Band, Long> {
    Optional<Band> findByBandName(String bandName);
}
