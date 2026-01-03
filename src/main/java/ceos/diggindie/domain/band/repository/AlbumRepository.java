package ceos.diggindie.domain.band.repository;

import ceos.diggindie.domain.band.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    boolean existsBySpotifyId(String spotifyId);
    Optional<Album> findBySpotifyId(String spotifyId);
}