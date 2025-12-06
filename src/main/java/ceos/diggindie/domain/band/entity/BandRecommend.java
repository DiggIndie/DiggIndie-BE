package ceos.diggindie.domain.band.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "band_recommend")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BandRecommend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_recommend_id")
    private Long id;

    private Integer priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "band_id", nullable = false)
    private Band band;

}