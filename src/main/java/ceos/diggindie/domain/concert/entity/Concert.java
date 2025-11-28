package ceos.diggindie.domain.concert.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "concert")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;

    @Column(name = "main_img", length = 200)
    private String mainImg;

    @Column(name = "main_url", length = 200)
    private String mainUrl;

    @Column(name = "book_url", length = 200)
    private String bookUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_hall_id", nullable = false)
    private ConcertHall concertHall;

    @OneToMany(mappedBy = "concert")
    private List<ArtistConcert> artistConcerts = new ArrayList<>();

    @OneToMany(mappedBy = "concert")
    private List<ConcertScrap> concertScraps = new ArrayList<>();

}