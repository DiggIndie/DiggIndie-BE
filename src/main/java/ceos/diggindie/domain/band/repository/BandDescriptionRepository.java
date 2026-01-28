package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.BandDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BandDescriptionRepository extends JpaRepository<BandDescription, Long> {
    
    Optional<BandDescription> findByBandId(Long bandId);
}
