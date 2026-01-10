package ceos.diggindie.domain.concert.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concert_scrap")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertScrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public ConcertScrap(Concert concert, Member member) {
        this.concert = concert;
        this.member = member;
    }
}