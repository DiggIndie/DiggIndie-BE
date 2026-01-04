package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, Long> {
    boolean existsBySpotifyId(String spotifyId);
}