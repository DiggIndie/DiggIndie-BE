package ceos.diggindie.domain.band.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "top_track")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@BatchSize(size = 100)
public class TopTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "top_track_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 300)
    private String externalUrl;

    @Builder
    public TopTrack(Band band, String title, String externalUrl) {
        this.band = band;
        this.title = title;
        this.externalUrl = externalUrl;
    }

    public void update(String title, String externalUrl) {
        this.title = title;
        this.externalUrl = externalUrl;
    }
}
