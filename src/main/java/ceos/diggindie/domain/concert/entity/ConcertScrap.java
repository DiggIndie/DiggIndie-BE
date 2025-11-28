package ceos.diggindie.domain.concert.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "concert_hall_id", nullable = false)
    private Long concertHallId;

}