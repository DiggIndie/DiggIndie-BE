package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "band_description")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BandDescription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_description_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // TODO: 나중에 converter로 구현 시 변경할 컬럼
    @Transient
    private float[] embedding;

    @Builder
    public BandDescription(Band band, String description) {
        this.band = band;
        this.description = description;
    }
}
