package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}

