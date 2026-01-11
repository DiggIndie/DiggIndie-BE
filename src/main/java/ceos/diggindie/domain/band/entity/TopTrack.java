package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "top_track")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopTrack extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "top_track_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "external_url", nullable = false, length = 300)
    private String externalUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false, unique = true)
    private Band band;

    @Builder
    public TopTrack(String title, String externalUrl, Band band) {
        this.title = title;
        this.externalUrl = externalUrl;
        this.band = band;
    }
}