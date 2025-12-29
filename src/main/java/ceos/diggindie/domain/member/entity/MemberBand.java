package ceos.diggindie.domain.member.entity;

import ceos.diggindie.domain.band.entity.Band;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id")
    private Band band;

    @Builder
    public MemberBand(Long memberId, Band band) {
        this.memberId = memberId;
        this.band = band;
    }

    public static MemberBand of(Long memberId, Band band) {
        return MemberBand.builder()
                .memberId(memberId)
                .band(band)
                .build();
    }
}