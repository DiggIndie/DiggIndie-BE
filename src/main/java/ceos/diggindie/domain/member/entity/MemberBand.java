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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id")
    private Band band;

    @Builder
    public MemberBand(Member member, Band band) {
        this.member = member;
        this.band = band;
    }

    public static MemberBand of(Member member, Band band) {
        return MemberBand.builder()
                .member(member)
                .band(band)
                .build();
    }
}