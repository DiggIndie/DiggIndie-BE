package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.keyword.entity.BandKeyword;
import ceos.diggindie.domain.magazine.entity.BandStory;
import ceos.diggindie.domain.member.entity.MemberBand;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

}