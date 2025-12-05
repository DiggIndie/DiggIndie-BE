package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "band_description")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BandDescription extends BaseEntity {

    @Id
    @Column(name = "band_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "band_id")
    private Band band;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // TODO: 나중에 converter로 구현 시 변경할 컬럼
    @Transient
    private float[] embedding;
}
