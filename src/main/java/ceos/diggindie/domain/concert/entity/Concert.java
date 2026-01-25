package ceos.diggindie.domain.concert.entity;

import ceos.diggindie.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(nullable = false, length = 200)  // 30 → 200 (제목 길이 늘림)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "preorder_price")
    private Integer preorderPrice;

    @Column(name = "on_site_price")
    private Integer onSitePrice;

    @Column(name = "main_img", length = 1000)
    private String mainImg;

    @Column(name = "main_url", length = 500)
    private String mainUrl;

    @Column(name = "book_url", length = 500)
    private String bookUrl;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_hall_id", nullable = false)
    private ConcertHall concertHall;

    @OneToMany(mappedBy = "concert")
    private List<BandConcert> bandConcerts = new ArrayList<>();

    @OneToMany(mappedBy = "concert")
    private List<ConcertScrap> concertScraps = new ArrayList<>();

    @Builder
    private Concert(String title, LocalDateTime startDate, LocalDateTime endDate,
                    String description, Integer preorderPrice, Integer onSitePrice,
                    String mainImg, String mainUrl, String bookUrl, ConcertHall concertHall) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.preorderPrice = preorderPrice;
        this.onSitePrice = onSitePrice;
        this.mainImg = mainImg;
        this.mainUrl = mainUrl;
        this.bookUrl = bookUrl;
        this.concertHall = concertHall;
        this.views = 0;
    }

    public void updatePrices(Integer preorderPrice, Integer onSitePrice) {
        this.preorderPrice = preorderPrice;
        this.onSitePrice = onSitePrice;
    }

    public void increaseViews() {
        this.views++;
    }
}