package ceos.diggindie.domain.concert.entity;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.domain.band.entity.Artist;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_hall_id", nullable = false)
    private ConcertHall concertHall;
}