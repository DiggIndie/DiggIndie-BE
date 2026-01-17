package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByBandId(Long bandId);
}

