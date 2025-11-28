package ceos.diggindie.domain.concert.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_concert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistConcert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_concert_id")
    private Long id;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(name = "concert_id", nullable = false)
    private Long concertId;

    @Column(name = "concert_hall_id", nullable = false)
    private Long concertHallId;

}