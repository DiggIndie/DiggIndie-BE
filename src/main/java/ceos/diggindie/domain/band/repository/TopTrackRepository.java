package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.TopTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopTrackRepository extends JpaRepository<TopTrack, Long> {
    Optional<TopTrack> findByBand(Band band);
    boolean existsByBand(Band band);
}

