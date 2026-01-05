package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "album_image", length = 300)
    private String albumImage;

    @Column(name = "spotify_id", length = 100)
    private String spotifyId;

    @Column(name = "release_date", length = 20)
    private String releaseDate;

    @Column(name = "album_type", length = 20)
    private String albumType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Music> musics = new ArrayList<>();

    @Builder
    public Album(String title, String albumImage, String spotifyId,
                 String releaseDate, String albumType, Band band) {
        this.title = title;
        this.albumImage = albumImage;
        this.spotifyId = spotifyId;
        this.releaseDate = releaseDate;
        this.albumType = albumType;
        this.band = band;
    }
}