package ceos.diggindie.domain.spotify.service;

import ceos.diggindie.domain.band.entity.Album;
import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.Music;
import ceos.diggindie.domain.band.repository.AlbumRepository;
import ceos.diggindie.domain.band.repository.MusicRepository;
import ceos.diggindie.domain.spotify.dto.SpotifyAlbumsResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTracksResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumSaveService {

    private final AlbumRepository albumRepository;
    private final MusicRepository musicRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAlbumWithTracks(SpotifyAlbumsResponse.AlbumItem spotifyAlbum,
                                    List<SpotifyTracksResponse.TrackItem> tracks,
                                    Band band) {

        if (albumRepository.existsBySpotifyId(spotifyAlbum.id())) {
            log.debug("이미 존재하는 앨범: {}", spotifyAlbum.name());
            return;
        }

        String albumImage = spotifyAlbum.images().stream()
                .findFirst()
                .map(SpotifyAlbumsResponse.Image::url)
                .orElse(null);

        Album album = Album.builder()
                .band(band)
                .title(spotifyAlbum.name())
                .spotifyId(spotifyAlbum.id())
                .releaseDate(spotifyAlbum.release_date())
                .albumType(spotifyAlbum.album_type())
                .albumImage(albumImage)
                .build();

        albumRepository.save(album);

        for (SpotifyTracksResponse.TrackItem track : tracks) {
            if (musicRepository.existsBySpotifyId(track.id())) {
                continue;
            }

            String spotifyUrl = track.external_urls() != null
                    ? track.external_urls().spotify()
                    : null;

            Music music = Music.builder()
                    .album(album)
                    .title(track.name())
                    .trackNumber(track.track_number())
                    .durationMs(track.duration_ms())
                    .previewUrl(track.preview_url())
                    .spotifyUrl(spotifyUrl)
                    .spotifyId(track.id())
                    .build();

            musicRepository.save(music);
        }

        log.debug("앨범 저장 완료: {} (트랙 {}개)", spotifyAlbum.name(), tracks.size());
    }
}