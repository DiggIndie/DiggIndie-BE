package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "music")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;

    @Column(name = "music_name", nullable = false, length = 150)
    private String title;

    @Column(name = "track_number")
    private Integer trackNumber;

    @Column(name = "duration_ms")
    private Integer durationMs;

    @Column(name = "preview_url", length = 300)
    private String previewUrl;

    @Column(name = "spotify_url", length = 300)
    private String spotifyUrl;

    @Column(name = "spotify_id", length = 100)
    private String spotifyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Builder
    public Music(String title, Integer trackNumber, Integer durationMs,
                 String previewUrl, String spotifyUrl, String spotifyId, Album album) {
        this.title = title;
        this.trackNumber = trackNumber;
        this.durationMs = durationMs;
        this.previewUrl = previewUrl;
        this.spotifyUrl = spotifyUrl;
        this.spotifyId = spotifyId;
        this.album = album;
    }
}