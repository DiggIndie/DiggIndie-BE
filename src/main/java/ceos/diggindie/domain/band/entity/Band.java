package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.keyword.entity.BandKeyword;
import ceos.diggindie.domain.magazine.entity.BandStory;
import ceos.diggindie.domain.member.entity.MemberBand;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "band")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Band extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_id")
    private Long id;

    @Column(name = "band_name", length = 20)
    private String bandName;

    @Column(name = "main_image", length = 200)
    private String mainImage;

    @Column(name = "main_url", length = 200)
    private String mainUrl;

    @Column(name = "main_music", length = 200)
    private String mainMusic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "spotify_id", length = 100)
    private String spotifyId;

    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Artist> artists = new ArrayList<>();

    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "band", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BandStory> artistStories = new ArrayList<>();

    @OneToMany(mappedBy = "band")
    private List<MemberBand> memberBands = new ArrayList<>();

    @OneToMany(mappedBy = "band")
    private List<BandKeyword> bandKeywords = new ArrayList<>();

    @OneToMany(mappedBy = "band")
    private List<BandRecommend> artistRecommends = new ArrayList<>();

    @OneToMany(mappedBy = "band")
    private List<BandScrap> artistScraps = new ArrayList<>();

    @Builder
    public Band(
            String bandName,
            String mainImage,
            String mainUrl,
            String mainMusic,
            String description,
            String spotifyId
    ) {
        this.bandName = bandName;
        this.mainImage = mainImage;
        this.mainUrl = mainUrl;
        this.mainMusic = mainMusic;
        this.description = description;
        this.spotifyId = spotifyId;
    }

}