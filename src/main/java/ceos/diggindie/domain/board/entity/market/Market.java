package ceos.diggindie.domain.board.entity.market;

import ceos.diggindie.common.entity.BaseEntity;
import ceos.diggindie.common.enums.MarketType;
import ceos.diggindie.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "market")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Market extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "chat_url", length = 300)
    private String chatUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer views = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MarketType type;

    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarketScrap> marketScraps = new ArrayList<>();

    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarketImage> marketImages = new ArrayList<>();

    @Builder
    public Market(String title, String content, Integer price, String chatUrl, Member member, MarketType type) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.chatUrl = chatUrl;
        this.member = member;
        this.type = type;
    }

    public void addImage(MarketImage image) {
        this.marketImages.add(image);
    }

    public void increaseViews() {
        this.views++;
    }

    public void update(String title, String content, Integer price, String chatUrl, MarketType type) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.chatUrl = chatUrl;
        this.type = type;
    }

    public void clearImages() {
        this.marketImages.clear();
    }


}